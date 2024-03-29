import {li, a, div, h1, input, label, button, form, h3, p} from "./generators/createElement.js";
import userUtils from "./user.js";
import boardGenerator from "./generators/boardGenerator.js";
import userGenerator from "./generators/userGenerator.js";
import homeGenerator from "./generators/homeGenerator.js";
import listGenerator from "./generators/listGenerator.js";
import cardGenerator from "./generators/cardGenerator.js";
import bootstrapGenerator from "./generators/bootstrapGenerator.js";
import appConstants from "./appConstants.js";



function populateNavbar(items) {
  const navbar = document.getElementById("navbar-items");
  navbar.innerHTML = "";

  items.forEach((i) => {
    navbar.appendChild(
      li(
        {
          class: "nav-item",
        },
        a(i.text, {
          href: i.href,
          class: "nav-link link-light",
        })
      )
    );
  });
}

function getHome(mainContent) {
  mainContent.innerHTML = "";
  mainContent.appendChild(homeGenerator.content());
  mainContent.appendChild(homeGenerator.creditsFooter());

  const navbarItems = [
    { href: "#userDetails", text: "User" },
    { href: "#searchboards", text: "Search" },
  ];

      const user = userUtils.getUser()
      if (user){
       navbarItems.push({ href: "#logout", text: "Logout" });
      }else{
         navbarItems.push( { href: "#signup", text: "Sign Up" });
         navbarItems.push( { href: "#login", text: "Login" });
      }

  populateNavbar(navbarItems);
}

function getUser(mainContent) {
  fetch(`${appConstants.API_BASE_URL}` + "users/" + userUtils.getId())
    .then((res) => res.json())
    .then((user) => {
      const content = div(
        {
          class: "container",
        },
        div(
          {
            class: "position-absolute top-50 start-50 translate-middle",
          },
          userGenerator.listingCard(user)
        )
      );
      mainContent.replaceChildren(content);
      const navbarItems = [{ href: "#searchboards", text: "Search" }];
      populateNavbar(navbarItems);
    });
}

function getBoards(mainContent) {
  const content = boardGenerator.boardCycle();
  mainContent.replaceChildren(content);
  const navbarItems = [
    { href: "#userDetails", text: "User" },
    { href: "#searchboards", text: "Search Boards" },
  ];
  populateNavbar(navbarItems);
}

function getSearchBoards(mainContent) {
  const content = boardGenerator.searchBoard(
    userUtils.getAuthorizationHeader()
  );

  mainContent.replaceChildren(content);

  const navbarItems = [{ href: "#userDetails", text: "User" }];
  populateNavbar(navbarItems);
}

function getBoardDetail(mainContent, params) {
    fetch(`${appConstants.API_BASE_URL}` + "boards/" + params.boardId, {
        headers: userUtils.getAuthorizationHeader(),
    })
        .then((res) => res.json())
        .then(async (board) => {
            const listsReq = await fetch(
                `${appConstants.API_BASE_URL}` + "boards/" + params.boardId + "/lists",
                {
                    headers: userUtils.getAuthorizationHeader(),
                }
            );
            const lists = await listsReq.json();
            console.log(lists)
            const content = div(
                { class: "container mt-4" },
                boardGenerator.details(board, lists, userUtils.getAuthorizationHeader())
            );

            mainContent.replaceChildren(content);

            const navbarItems = [
                {href: "#searchboards", text: "Search Boards"},
                {href: "#board/" + params.boardId + "/user-list", text: "Users"},
            ];
            populateNavbar(navbarItems);
        });
}


function getBoardsUsers(mainContent, params) {
  fetch(`${appConstants.API_BASE_URL}` + "boards/" + params.boardId + "/user-list", {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((users) => {
      const content = div(
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
  fetch(`${appConstants.API_BASE_URL}` + "boards/" + params.boardId + "/lists/" + params.listId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then(async (list) => {
      const cardsReq = await fetch(
        `${appConstants.API_BASE_URL}` +
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

      const content = div(
        { class: "container" },
        listGenerator.details(params.boardId, list, cards, userUtils.getAuthorizationHeader())
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
  fetch(`${appConstants.API_BASE_URL}` + "boards/" + params.boardId + "/cards/" + params.cardId, {
    headers: userUtils.getAuthorizationHeader(),
  })
    .then((res) => res.json())
    .then((card) => {
      const content = div(
        { class: "container" },
        div(
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

function getSignUp(mainContent) {
    const content = userGenerator.signUpContent()
    mainContent.replaceChildren(content);
    const links = [
        {
            href: "#home",
            text: "Home",
        },
    ];
    populateNavbar(links);
}
function logout(mainContent){
 sessionStorage.clear();
 window.location.href = "/#home";
}
function getLoginForm(mainContent) {
    const content = userGenerator.loginFormContent()
    mainContent.replaceChildren(content);
    const links = [
        {
            href: "#home",
            text: "Home",
        },
    ];
    populateNavbar(links);
}


const handlers = {
      getHome,
      getUser,
      getBoards,
      getBoardDetail,
      getBoardsUsers,
      listDetails,
      cardDetail,
    getSearchBoards,
    getSignUp,
    logout,
    getLoginForm
};

export default handlers;
