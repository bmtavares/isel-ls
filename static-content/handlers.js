import * as c from "./createElement.js";
import userUtils from "./user.js";
const API_BASE_URL = "http://localhost:9000/";

function createLinks(links) {
  const linksEl = document.getElementById("links");
  linksEl.innerHTML = "";

  links.forEach((link) => {
    const anchor = document.createElement("a");
    anchor.href = link.href;
    anchor.className = `link-${link.type}`;
    anchor.innerText = link.text;

    linksEl.appendChild(anchor);
  });
}

function getHome(mainContent) {
  const h1 = document.createElement("h1");
  const text = document.createTextNode("Home");
  h1.appendChild(text);
  mainContent.replaceChildren(h1);
  const links = [
    { href: "#userDetails", type: "primary", text: "user details" },
    { href: "#boards", type: "primary", text: "Boars" },
  ];
  createLinks(links);
}

function getUser(mainContent) {
  fetch(API_BASE_URL + "users/1")
    .then((res) => res.json())
    .then((user) => {
      const ulStd = document.createElement("ul");

      const liName = document.createElement("li");
      const textName = document.createTextNode("Name : " + user.name);
      liName.appendChild(textName);

      const liNumber = document.createElement("li");
      const textNumber = document.createTextNode("Number : " + user.email);
      liNumber.appendChild(textNumber);

      ulStd.appendChild(liName);
      ulStd.appendChild(liNumber);

      mainContent.replaceChildren(ulStd);

      const links = [
        { href: "#home", type: "secondary", text: "Home" },
        { href: "#boards", type: "primary", text: "Boars" },
      ];

      createLinks(links);
    });
}

function boardApend(item) {
  const ulStd = document.createElement("ul");
  const liName = document.createElement("li");
  const textName = document.createTextNode("Name : " + item.name);

  liName.appendChild(textName);
  ulStd.appendChild(liName);

  const detailsLink = document.createElement("a");
  detailsLink.href = "#boards/" + item.id; // modify the URL path to include the board ID
  detailsLink.innerHTML = "link";

  const par = document.createElement("p");
  par.innerHTML = "details: ";
  par.appendChild(detailsLink);

  detailsLink.addEventListener("click", function (event) {
    event.preventDefault(); // prevent the link from navigating away from the page
    window.history.pushState(null, null, detailsLink.href); // update the URL without navigating
    location.reload();
  });

  par.appendChild(detailsLink);
  mainContent.appendChild(ulStd);
  mainContent.appendChild(par);
}

function getBoards(mainContent) {
  fetch(API_BASE_URL + "boards", {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((boards) => {
      const ulStd = document.createElement("ul");
      const lititle = document.createElement("li");

      const textTitle = document.createTextNode("__BOARDS __");
      ulStd.appendChild(lititle);

      lititle.appendChild(textTitle);
      mainContent.replaceChildren(ulStd);
      boards.forEach(boardApend);

      const links = [
        { href: "#home", type: "secondary", text: "Home" },
        { href: "#userDetails", type: "primary", text: "user details" },
      ];
      createLinks(links);
    });
}

function addListsToPage(itemList) {
  const ulStd = document.createElement("ul");
  const liName = document.createElement("li");
  const textName = document.createTextNode("Name : " + itemList.name);

  liName.appendChild(textName);
  ulStd.appendChild(liName);

  const detailsLink = document.createElement("a");
  detailsLink.href = "#boards/" + itemList.boardId + "/lists/" + itemList.id; // modify the URL path to include the board ID
  detailsLink.innerHTML = "link";

  const par = document.createElement("p");
  par.innerHTML = "details: ";
  par.appendChild(detailsLink);

  detailsLink.addEventListener("click", function (event) {
    event.preventDefault(); // prevent the link from navigating away from the page
    window.history.pushState(null, null, detailsLink.href); // update the URL without navigating
    location.reload();
  });
  par.appendChild(detailsLink);
  mainContent.appendChild(ulStd);
  mainContent.appendChild(par);
}

function getBoardDetail(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((board) => {
      const ulStd = document.createElement("ul");

      const liName = document.createElement("li");
      const textName = document.createTextNode("Name : " + board.name);
      liName.appendChild(textName);

      const liNumber = document.createElement("li");
      const textNumber = document.createTextNode(
        "description : " + board.description
      );
      liNumber.appendChild(textNumber);
      ulStd.appendChild(liName);
      ulStd.appendChild(liNumber);
      mainContent.replaceChildren(ulStd);

      fetch(API_BASE_URL + "boards/" + params.boardId + "/lists", {
        headers: userUtils.getAuthorizationHeader(),
      })
        .then((res) => res.json())
        .then((lists) => {
          lists.forEach(addListsToPage);
        });
      const links = [
        { href: "#boards", type: "secondary", text: "boards" },
        {
          href: "#board/" + params.boardId + "/user-list",
          type: "primary",
          text: "board_useres",
        },
        { href: "#List_details", type: "secondary", text: "List_details" },
      ];
      createLinks(links);
    });
}

function userApend(item) {
  const mainContent = document.getElementById("mainContent");
  const ulStd = document.createElement("ul");
  const liName = document.createElement("li");
  const textName = document.createTextNode("Name : " + item.name);
  const textemail = document.createTextNode("Email : " + item.email);

  liName.appendChild(textName);
  liName.appendChild(textemail);

  ulStd.appendChild(liName);
  mainContent.appendChild(ulStd);
}

function getBoardsUsers(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId + "/user-list", {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((user) => {
      mainContent.innerHTML = "";
      user.forEach(userApend);

      const links = [
        {
          href: "#boards/" + params.boardId,
          type: "secondary",
          text: "boards details",
        },
      ];

      createLinks(links);
    });
}

function addCardsToPage(card) {
  const mainContent = document.getElementById("mainContent");
  const main = c.div(c.h3("card: " + card.name));
  const detailsLink = document.createElement("a");
  detailsLink.href =
    "#boards/" + card.boardId + "/lists/" + card.listId + "/cards/" + card.id;
  detailsLink.innerHTML = card.name;
  main.appendChild(detailsLink);

  mainContent.appendChild(main);
}

function listDetails(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId + "/lists/" + params.listId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((list) => {
      mainContent.innerHTML = "";
      const main = c.div(c.h1("lista: " + list.name));
      mainContent.appendChild(main);
      fetch(
        API_BASE_URL +
          "boards/" +
          params.boardId +
          "/lists/" +
          params.listId +
          "/cards",
        {
          headers: userUtils.getAuthorizationHeader(),
        }
      )
        .then((res) => res.json())
        .then((cards) => cards.forEach(addCardsToPage));
      const links = [
        {
          href: "#boards/" + params.boardId,
          type: "secondary",
          text: "boards details",
        },
      ];
      createLinks(links);
    });
}

function cardDetail(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId + "/cards/" + params.cardId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((card) => {
      mainContent.innerHTML = "";
      const date = new Date(card.dueDate);
      const main = c.div(
        c.h1("card: " + card.name),
        c.li(card.description),
        c.li(
          `due date: ${date.getFullYear()}  ${date.getMonth()} ${date.getDate()}`
        )
      );
      mainContent.appendChild(main);
      const links = [
        {
          href: "#boards/" + params.boardId + "/lists/" + params.listId,
          type: "secondary",
          text: "list details",
        },
      ];
      createLinks(links);
    });
}

const handlers = {
  getHome,
  getUser,
  getBoards,
  getBoardDetail,
  getBoardsUsers,
  listDetails,
  cardDetail,
};

export default handlers;
