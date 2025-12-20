const http = require('http');
const { Client } = require('@stomp/stompjs');
const SockJS = require('sockjs-client');

// Configuration
const GATEWAY_URL = 'http://localhost:8000';
const USER_EMAIL = `testuser_${Date.now()}@example.com`;
const USER_PASSWORD = 'password123';

// Helper for HTTP Requests
function request(method, path, body, token = null) {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: 'localhost',
            port: 8000,
            path: path,
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        };

        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }

        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', (chunk) => data += chunk);
            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    try {
                        resolve(data ? JSON.parse(data) : {});
                    } catch (e) {
                        resolve(data);
                    }
                } else {
                    reject(new Error(`Request failed with status ${res.statusCode}: ${data}`));
                }
            });
        });

        req.on('error', (e) => reject(e));
        if (body) req.write(JSON.stringify(body));
        req.end();
    });
}

async function runTest() {
    console.log('🚀 Starting End-to-End Test...');

    let token;

    try {
        // 1. Register
        console.log(`\n1. Registering user: ${USER_EMAIL}`);
        await request('POST', '/api/auth/register', {
            name: 'Test User',
            email: USER_EMAIL,
            password: USER_PASSWORD
        });
        console.log('✅ Registration successful');

        // 2. Login
        console.log('\n2. Logging in...');
        const loginResponse = await request('POST', '/api/auth/login', {
            email: USER_EMAIL,
            password: USER_PASSWORD
        });
        token = loginResponse.token;
        if (!token) throw new Error('No token received from login');
        console.log('✅ Login successful. Token received.');

        // 3. Create Portfolio
        console.log('\n3. Creating Portfolio...');
        const portfolio = await request('POST', '/api/portfolios', {
            name: 'My Test Portfolio',
            description: 'Created by E2E test'
        }, token);
        console.log('✅ Portfolio created:', portfolio);

        // 4. Test WebSocket
        console.log('\n4. Testing WebSocket Connection...');
        await testWebSocket(token);

    } catch (error) {
        console.error('❌ Test Failed:', error.message);
        process.exit(1);
    }
}

function testWebSocket(token) {
    return new Promise((resolve, reject) => {
        const client = new Client({
            webSocketFactory: () => new SockJS(`${GATEWAY_URL}/ws?token=${encodeURIComponent(token)}`),
            reconnectDelay: 0,
            debug: () => {} // Suppress debug logs
        });

        let timeout = setTimeout(() => {
            client.deactivate();
            reject(new Error('WebSocket timeout: No message received within 10s'));
        }, 10000);

        client.onConnect = function(frame) {
            console.log('   [✓] STOMP connected');
            
            // Subscribe to AAPL (works with Finnhub demo key)
            client.subscribe('/topic/prices/AAPL', function(message) {
                console.log('   [✓] Received message:', message.body);
                clearTimeout(timeout);
                client.deactivate();
                resolve();
            });
            
            console.log('   [i] Subscribed to /topic/prices/AAPL. Waiting for data...');
        };

        client.onStompError = function(frame) {
            clearTimeout(timeout);
            reject(new Error('STOMP error: ' + frame.headers['message']));
        };

        client.activate();
    });
}

runTest();
