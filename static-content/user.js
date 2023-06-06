const userData = {
  id: 1,
  token: "f52129ca-ccf1-42cc-a363-fdc89f71901b",
};
const tokenKey = "token"
const userIdKey = "userId"
const userNameKey = "userName"
function setToken(newToken) {
  if (typeof newToken === "string") {
    sessionStorage.setItem(tokenKey,newToken)
  }
}

function removeToken() {
  sessionStorage.removeItem(tokenKey)
}

function getToken() {
  return sessionStorage.getItem(tokenKey);
}

function getId() {
  return sessionStorage.getItem(userIdKey);
}
function setId(id) {
  return sessionStorage.setItem(userIdKey,id);
}
function getUsername() {
  return sessionStorage.getItem(userNameKey);
}
function setUsername(name) {
  return sessionStorage.setItem(userNameKey,name);
}
function getAuthorizationHeader() {
  return { Authorization: `Bearer ${getToken()}` };
}

function setUser(token,id,name){
  setToken(token)
  setId(id)
  setUsername(name)
}
function getUser(){
  const token = getToken()
  const id = getId()
  const name = getUsername()
  if (token === null) return null
  else return {"token":token,"userId":id,"name":name}
}
const userUtils = {
  getAuthorizationHeader,
  getToken,
  getId,
  setToken,
  setUser,
  removeToken,
  setId,
  getUser
};

export default userUtils;
