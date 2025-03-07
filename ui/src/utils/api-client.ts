import axios from "axios";
import { Dialog, Toast } from "@halo-dev/components";

const baseURL = import.meta.env.VITE_API_URL || "";

export const apiClient = axios.create({
  baseURL,
  withCredentials: true,
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证信息等
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        // 未授权处理
        Toast.error("未授权，请重新登录");
      } else if (status === 403) {
        // 无权限处理
        Toast.error("无权限执行此操作");
      } else if (status === 400) {
        // 请求参数错误
        const message = data.message || "请求参数错误";
        Toast.error(message);
      } else if (status === 404) {
        // 资源不存在
        Toast.error("请求的资源不存在");
      } else if (status === 500) {
        // 服务器错误
        Toast.error("服务器错误，请稍后再试");
      } else {
        // 其他错误
        Toast.error(data.message || "请求失败");
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      Toast.error("网络错误，请检查您的网络连接");
    } else {
      // 请求配置错误
      Toast.error("请求配置错误");
    }

    return Promise.reject(error);
  }
); 