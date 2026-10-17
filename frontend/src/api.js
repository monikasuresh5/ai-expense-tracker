import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api" });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (error) => {
    const isAuthCall = error.config?.url?.startsWith("/users/");
    if (error.response?.status === 401 && !isAuthCall) {
      localStorage.clear();
      window.location.reload();
    }
    return Promise.reject(error);
  }
);

export default api;