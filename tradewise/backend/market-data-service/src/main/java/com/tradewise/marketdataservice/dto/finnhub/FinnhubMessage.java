package com.tradewise.marketdataservice.dto.finnhub;

import lombok.Data;

import java.util.List;

@Data
public class FinnhubMessage {
    private String type;
    private List<FinnhubTrade> data;
}