const userData = {
  token: "26db00bb-4579-4cbb-86d3-93c0e702b8c6",
};

function setToken(newToken) {
  if (typeof newToken === "string") userData.token = newToken;
}

function removeToken() {
  userData.token = null;
}

function getToken() {
  return userData.token;
}

function getAuthorizationHeader() {
  return { Authorization: `Bearer ${userData.token}` };
}

const userUtils = {
  getAuthorizationHeader,
};

export default userUtils;
