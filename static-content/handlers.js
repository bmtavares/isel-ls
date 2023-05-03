import * as c from "./createElement.js";
import userUtils from "./user.js";
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

  // const h1 = document.createElement("h1");
  // const text = document.createTextNode("Home");
  // h1.appendChild(text);
  // mainContent.replaceChildren(h1);

  const navbarItems = [
    { href: "#userDetails", text: "User" },
    { href: "#boards", text: "Boards" },
  ];
  populateNavbar(navbarItems);
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

      const navbarItems = [{ href: "#boards", text: "Boards" }];
      populateNavbar(navbarItems);
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

      const navbarItems = [{ href: "#userDetails", text: "User" }];
      populateNavbar(navbarItems);
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
