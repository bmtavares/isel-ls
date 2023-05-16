const userData = {
  id: 1,
  token: "f52129ca-ccf1-42cc-a363-fdc89f71901b",
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

function getId() {
  return userData.id;
}

function getAuthorizationHeader() {
  return { Authorization: `Bearer ${userData.token}` };
}

const userUtils = {
  getAuthorizationHeader,
  getToken,
  getId
};

export default userUtils;
