import axios from 'axios';

const API_BASE_URL = 'http://192.168.0.28:8080/api';    // Local network IPV4 for mobile device testing
// Android emulator uses 10.0.2.2 for localhost
// http://10.0.2.2:8080/api

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Basic ' + btoa('user:user123')
  }
});

export default api;
