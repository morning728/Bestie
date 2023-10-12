import axios, { AxiosResponse } from "axios";


type StoredToken = {
  value: string;
  timeStamp: number;
};

const TOKEN_KEY = 'auth_token';
/**
 * Токен имеет фиксированное время жизни.
 * Важно, так как храним в localStorage и уменьшаем риск в случае xss.
 * Время жизни токена ставим в 23 часа 59 минут
 */
const TOKEN_TTL_MS = 86340000;

const isExpired = (timeStamp?: number): boolean => {
  if (!timeStamp) return false;

  const now = new Date().getTime();
  const diff = now - timeStamp;

  return diff > TOKEN_TTL_MS;
};

const setToken = (access_token: string): void => {
  localStorage.setItem(
    TOKEN_KEY,
    JSON.stringify({
      value: access_token,
      timeStamp: new Date().getTime(),
    })
  );
};

const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY);
};

const getToken = (): StoredToken | null => {
  let result = null;

  const storedToken = localStorage.getItem(TOKEN_KEY);
  storedToken && (result = JSON.parse(storedToken));

  return result;
};

const doOrdinaryRequest = async (url, body = null, type, par = null): Promise<AxiosResponse<any, any>> => {
  const headers = {}
  const token = getToken();
  if(token != null && !isExpired(token.timeStamp)){
    headers['Authorization'] = 'Bearer ' + token.value
  } else {
    throw new Error("invalid token");
  }

  if (type == "get") {
    if (par != null) {
      return await axios.get(`${url}${par}`, {
        headers: headers
      });
    }
    return await axios.get(url, {
      headers: headers
    });
  } else if (type == "post") {
    if (par != null) {
      return await axios.post(`${url}${par}`, body, {
        headers: headers
      });
    }
    return await axios.post(url, body, {
      headers: headers
    });
  } else if (type == "delete") {
    //headers['Content-Type'] = 'text/plain;charset=utf-8';
    if (par != null) {
      return await axios.delete(`${url}${par}`, {
        headers: headers
      });
    }
    return await axios.delete(url, {
      headers: headers
    });
  } else if (type == "put") {
    if (par != null) {
      return await axios.put(`${url}${par}`, body, {
        headers: headers
      });
    }
    return await axios.put(url, body, {
      headers: headers
    });
  } else {
    throw new Error("invalid type");
  }
}



export { getToken, setToken, removeToken, isExpired, doOrdinaryRequest };