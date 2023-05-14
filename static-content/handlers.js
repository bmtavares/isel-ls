import * as c from "./createElement.js";
import userUtils from "./user.js";
import boardGenerator from "./boardGenerator.js";
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

  mainContent.appendChild(
    c.div(
      {
        class: "container-fluid text-center",
      },
      c.div(
        {
          class: "row justify-content-center",
        },

        c.h1("Welcome to LEIC Fauxllo!"),
        c.p(
          userUtils.getToken()
            ? "You appear to be logged in!"
            : "Look's like you're not logged in..",
          {
            class: "text-muted",
          }
        )
      )
    )
  );

  mainContent.appendChild(
    c.footer(
      {
        class: "fixed-bottom isel-bg-colour ms-1 mb-1 me-1 rounded",
      },
      c.ul(
        {
          class: "nav justify-content-center",
        },
        c.li(
          {
            class: "nav-item",
          },
          c.a("Made in Chelas by", {
            class: "nav-link text-body text-opacity-70",
          })
        ),
        c.li(
          {
            class: "nav-item",
          },
          c.a("Manuel Fonseca", {
            class: "nav-link link-light",
            href: "https://github.com/manuel-48052",
          })
        ),
        c.li(
          {
            class: "nav-item",
          },
          c.a("Sérgio Zorro", {
            class: "nav-link link-light",
            href: "https://github.com/sergiomiguelzorro",
          })
        ),
        c.li(
          {
            class: "nav-item",
          },
          c.a("Bruno Tavares", {
            class: "nav-link link-light",
            href: "https://github.com/bmtavares",
          })
        )
      )
    )
  );

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
            class: "card position-absolute top-50 start-50 translate-middle",
          },
          c.div(
            {
              class: "card-body",
            },
            c.h3(`${user.name}`, {
              class: "card-title",
            }),
            c.p(`✉ ${user.email}`, {
              class: "card-text",
            })
          )
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

      const navbarItems = [
        {
          href: "#boards/" + params.boardId,
          text: "Board",
        },
      ];
      populateNavbar(navbarItems);
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
