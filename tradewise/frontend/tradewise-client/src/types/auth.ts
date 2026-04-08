export type UserResponse = {
  id: string;
  email: string;
  createdAt: string;
};

export type LoginResponse = {
  token: string;
  user: UserResponse;
};
