import * as c from "./createElement.js";
import userUtils from "./user.js";
import boardGenerator from "./boardGenerator.js";
import userGenerator from "./userGenerator.js";
import homeGenerator from "./homeGenerator.js";
import listGenerator from "./listGenerator.js";
import cardGenerator from "./cardGenerator.js";
const API_BASE_URL = "http://localhost:9000/";

function populateNavbar(items) {
  const navbar = document.getElementById("navbar-items");
  navbar.innerHTML = "";

  items.forEach((i) => {
    navbar.appendChild(
      c.li(
        {
          class: "nav-item",
        },
        c.a(i.text, {
          href: i.href,
          class: "nav-link link-light",
        })
      )
    );
  });
}

function getHome(mainContent) {
  mainContent.innerHTML = "";
  mainContent.appendChild(homeGenerator.content(userUtils.getToken()));
  mainContent.appendChild(homeGenerator.footer());

  const navbarItems = [
    { href: "#userDetails", text: "User" },
    { href: "#boards", text: "Boards" },
  ];
  populateNavbar(navbarItems);
}

function getUser(mainContent) {
  fetch(API_BASE_URL + "users/" + userUtils.getId())
    .then((res) => res.json())
    .then((user) => {
      const content = c.div(
        {
          class: "container",
        },
        c.div(
          {
            class: "position-absolute top-50 start-50 translate-middle",
          },
          userGenerator.listingCard(user)
        )
      );

      mainContent.replaceChildren(content);
    });
}

function getBoards(mainContent) {
  fetch(API_BASE_URL + "boards", {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((boards) => {
      const content = c.div(
        {class: "container px-2 py-4"},
        c.h1("Boards"),
        boardGenerator.listing(boards)
      )

      mainContent.replaceChildren(content);

      const navbarItems = [{ href: "#userDetails", text: "User" }];
      populateNavbar(navbarItems);
    });
}

function getBoardDetail(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then(async (board) => {
      const listsReq = await fetch(
        API_BASE_URL + "boards/" + params.boardId + "/lists",
        {
          headers: userUtils.getAuthorizationHeader(),
        }
      );
      const lists = await listsReq.json();

      const content = c.div(
        { class: "container" },
        boardGenerator.details(board, lists)
      );

      mainContent.replaceChildren(content);

      const navbarItems = [
        { href: "#boards", text: "Boards" },
        {
          href: "#board/" + params.boardId + "/user-list",
          text: "Users",
        },
      ];
      populateNavbar(navbarItems);
    });
}

function getBoardsUsers(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId + "/user-list", {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((users) => {
      const content = c.div(
        { class: "container px-2 py-4" },
        userGenerator.listing(users)
      );

      mainContent.replaceChildren(content);

      const navbarItems = [
        {
          href: "#boards/" + params.boardId,
          text: "Board",
        },
      ];
      populateNavbar(navbarItems);
    });
}

function listDetails(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId + "/lists/" + params.listId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then(async (list) => {
      const cardsReq = await fetch(
        API_BASE_URL +
          "boards/" +
          params.boardId +
          "/lists/" +
          params.listId +
          "/cards",
        {
          headers: userUtils.getAuthorizationHeader(),
        }
      );

      const cards = await cardsReq.json();

      const content = c.div(
        { class: "content" },
        listGenerator.details(list, cards)
      );

      mainContent.replaceChildren(content);

      const navbarItems = [
        {
          href: "#boards/" + params.boardId,
          text: "Board",
        },
      ];
      populateNavbar(navbarItems);
    });
}

function cardDetail(mainContent, params) {
  fetch(API_BASE_URL + "boards/" + params.boardId + "/cards/" + params.cardId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((card) => {
      const content = c.div(
        { class: "container" },
        c.div(
          {
            class: "position-absolute top-50 start-50 translate-middle",
          },
          cardGenerator.details(card)
        )
      );
      mainContent.replaceChildren(content);
      
      const links = [
        {
          href: "#boards/" + params.boardId + "/lists/" + params.listId,
          text: "List",
        },
      ];
      populateNavbar(links);
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
