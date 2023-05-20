import {li, a, div, h1, input, label, button, form, h3, p} from "./generators/createElement.js";
import userUtils from "./user.js";
import boardGenerator from "./generators/boardGenerator.js";
import userGenerator from "./generators/userGenerator.js";
import homeGenerator from "./generators/homeGenerator.js";
import listGenerator from "./generators/listGenerator.js";
import cardGenerator from "./generators/cardGenerator.js";
const API_BASE_URL = "http://localhost:9000/";


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
  mainContent.appendChild(homeGenerator.content(userUtils.getToken()));
  mainContent.appendChild(homeGenerator.creditsFooter());

  const navbarItems = [
    { href: "#userDetails", text: "User" },
    { href: "#searchboards", text: "Boards" },
  ];
  populateNavbar(navbarItems);
}

function getUser(mainContent) {
  fetch(API_BASE_URL + "users/" + userUtils.getId())
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
      const navbarItems = [{ href: "#boards", text: "boards" }];
      populateNavbar(navbarItems);
    });
}
function getBoards(mainContent) {
    let boards =  JSON.parse(localStorage.getItem("searchBoardsResult"))
    let idx = localStorage.getItem("boardIdx")
    if (idx === null){
        idx = 0
        localStorage.setItem("boardIdx",idx.toString())
    }else {
        idx = Number(idx)
    }
    let board = boards[idx]
    let prev;
    let next;
    if(idx-1<0){ prev = boards.length-1} else prev = idx-1
    if(idx+1>=boards.length){ next = 0} else next = idx+1
    const content =  div( {class: "container",style:"width: 500px"},
        div(
            {class: "d-flex justify-content-between"},
            div(
                button("<<Previous", { class: "btn btn-link",events:{
                        click:async (e) => {
                            e.preventDefault()
                            localStorage.setItem("boardIdx",prev.toString())
                            window.location.reload()
                            } } } )
            ),
            div(
                button("Next>>", { class: "btn btn-link" ,events:{
                        click:async (e) => {
                            e.preventDefault()
                            localStorage.setItem("boardIdx",next.toString())
                            window.location.reload()
                        } } })
            )
        ),
        div(
            { class: "card text-center" },
            div(
                { class: "card-header d-grid" },
                h3(board.name, { class: "card-title" }),
            ),
            div(
                { class: "card-body" },
                p(board.description, { class: "card-text" })
            ),
            div(
                { class: "card-footer d-grid" },
                a("View", { class: "btn btn-primary",style:"width: 200px", href: `#boards/${board.id}` })
            )
        )
    )




    mainContent.replaceChildren(content);
    const navbarItems = [{ href: "#userDetails", text: "User" }];
    populateNavbar(navbarItems);
}
// function getBoards(mainContent) {
//   fetch(API_BASE_URL + "boards", {
//     headers: userUtils.getAuthorizationHeader(),
//   })
//     .then((res) => res.json())
//     .then((boards) => {
//       const content = div(
//         { class: "container px-2 py-4" },
//         h1("Boards"),
//         boardGenerator.listing(boards)
//       );
//
//       mainContent.replaceChildren(content);
//
//       const navbarItems = [{ href: "#userDetails", text: "User" }];
//       populateNavbar(navbarItems);
//     });
// }
function getSearchBoards(mainContent) {

            const content = form(
                {class:"form-inline"},
                div(
                { class: "form-group mb-2" },
                label("Search Boards"),
                input( {type:"text", class:"form-control", id:"searchBoxInput", placeholder:"example group name"})),
                button("Search Boards",{type:"submit" ,class:"btn btn-primary",events:{
                    click:async (e) => {
                        e.preventDefault()
                        const a = document.getElementById("searchBoxInput")
                        const boards = await fetch(API_BASE_URL + `boards?search=${a.value}`,{headers: userUtils.getAuthorizationHeader(),
                    })
                            .then((res) => res.json())
                            .then((boards)=>{
                                localStorage.setItem("searchBoardsResult",JSON.stringify(boards))
                                localStorage.setItem("boardIdx",String(0))
                                window.location.hash = "boards"
                            })
                    }
                    }})
                );

            mainContent.replaceChildren(content);

            const navbarItems = [{ href: "#userDetails", text: "User" }];
            populateNavbar(navbarItems);
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

      const content = div(
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

      const content = div(
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



const handlers = {
      getHome,
      getUser,
      getBoards,
      getBoardDetail,
      getBoardsUsers,
      listDetails,
      cardDetail,
    getSearchBoards
};

export default handlers;
