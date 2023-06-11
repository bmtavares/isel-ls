import {
  div,
  h1,
  h3,
  p,
  a,
  button,
  form,
  label,
  input,
  h2,
} from "./createElement.js";
import listGenerator from "./listGenerator.js";
import userUtils from "../user.js";
const API_BASE_URL = "http://localhost:9000/";

import { generateFormModal } from "./formGenerator.js";
import bootstrapGenerator from "./bootstrapGenerator.js";
import appConstants from "../appConstants.js";

function listingCard(board) {
  return div(
    { class: "card text-center" },
    div(
      { class: "card-body" },
      h3(board.name, { class: "card-title" }),
      p(board.description, { class: "card-text" })
    ),
    div(
      { class: "card-footer d-grid" },
      a("View", { class: "btn btn-primary", href: `#boards/${board.id}` })
    )
  );
}

function listing(boards) {
  return div(
    { class: "row gy-2" },
    ...boards.map((board) => div({ class: "col-lg-4" }, listingCard(board)))
  );
}

function details(board, lists, authHeader) {
  localStorage.setItem("GlobalLists", JSON.stringify(lists));
  return div(
    listGenerator.createFormModal(authHeader, board.id),
    h1(board.name),
    h3(board.description, { class: "text-muted" }),
    bootstrapGenerator.generateModalButton(
      "Create new",
      "create-list-modal",
      "success"
    ),
    listGenerator.listing(lists)
  );
}

function boardCycle() {
  async function handleOnPreviousClick(e) {
    e.preventDefault();
    localStorage.setItem("boardIdx", prev.toString());
    window.dispatchEvent(new HashChangeEvent("hashchange"));
  }

  async function handleOnNextClick(e) {
    e.preventDefault();
    localStorage.setItem("boardIdx", next.toString());
    window.dispatchEvent(new HashChangeEvent("hashchange"));
  }

  const boards = JSON.parse(localStorage.getItem("searchBoardsResult"));

  let idx = localStorage.getItem("boardIdx");
  if (idx === null || idx === undefined) {
    idx = 0;
    localStorage.setItem("boardIdx", idx.toString());
  } else {
    idx = Number(idx);
  }

  let board = boards[idx];
  
  const prev = idx - 1 < 0 ? boards.length - 1 : idx - 1;
  const next = idx + 1 >= boards.length ? 0 : idx + 1;

  return div(
    { class: "container mt-4" },
    div(
      { class: "d-flex justify-content-between" },
      button("<<Previous", {
        class: "btn btn-link",
        events: {
          click: handleOnPreviousClick,
        },
      }),
      label(`${idx + 1}/${boards.length}`),
      button("Next>>", {
        class: "btn btn-link",
        events: {
          click: handleOnNextClick,
        },
      })
    ),
    div(
      { class: "card text-center" },
      div(
        { class: "card-header d-grid" },
        h3(board.name, { class: "card-title" })
      ),
      div({ class: "card-body" }, p(board.description, { class: "card-text" })),
      div(
        { class: "card-footer" },
        div(
          { class: "d-flex justify-content-between" },
          a("View", {
            class: "btn btn-info",
            style: "width: 100px",
            href: `#boards/${board.id}`,
          }),
          // a("+ Add", {
          //   class: "btn btn-success",
          //   style: "width: 100px",
          //   href: `#boards`,
          // })
        )
      )
    )
  );
}

function searchBoard(authHeader) {
  const id = "create-board";

  async function handleOnSearchClick(e) {
    e.preventDefault();
    const a = document.getElementById("searchBoxInput");
    await fetch(appConstants.API_BASE_URL + `boards?search=${a.value}`, {
      headers: userUtils.getAuthorizationHeader(),
    })
      .then((res) => res.json())
      .then((boards) => {
        if (boards.length === 0) {
          const label = document.getElementById("board-search-failed-label");
          label.innerText = "No boards found for this query. Please try again.";
          return; // Early return since we got no results
        }

        localStorage.setItem("searchBoardsResult", JSON.stringify(boards));
        localStorage.setItem("boardIdx", "0");
        location.hash = "boards";
      });
  }

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.target); // Using the FormData object we can quickly fetch all the inputs
    const data = JSON.stringify(Object.fromEntries(formData.entries())); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards`, {
      method: "post",
      headers: { ...authHeader, "Content-Type": "application/json" },
      body: data,
    });

    const responseData = await creationReq.json();
    if (!creationReq.ok) {
      const errorAlert = div(
        responseData.message,
        {
          class: "alert alert-danger alert-dismissible fade show",
          role: "alert",
        },
        button({
          class: "btn-close",
          "data-bs-dismiss": "alert",
          type: "button",
        })
      );
      document.querySelector("#board-form").prepend(errorAlert);
      return;
    }

    bootstrap.Modal.getInstance(document.querySelector(`#${id}-modal`)).hide(); // Hide the form modal

    e.target.reset();
  }

  return div(
    { class: "container mt-4" },
    bootstrapGenerator.generateModal(
      id,
      generateFormModal(
        "board",
        handleOnSubmitCreate,
        [
          { type: "text", name: "Name", required: true },
          { type: "text", name: "Description", required: true },
        ],
        "Create"
      ),
      "Create a new board"
    ),
    bootstrapGenerator.generateModalButton(
      "Create new",
      `${id}-modal`,
      "success"
    ),
    form(
      { class: "form-inline" },
      div(
        { class: "form-group mb-2 text-center" },
        label("Search Boards:"),
        input({
          type: "text",
          class: "form-control",
          id: "searchBoxInput",
          placeholder: "type group name here",
        })
      ),
      div(
        { class: "text-center" },
        button("Search Boards", {
          type: "submit",
          class: "btn btn-primary",
          events: {
            click: handleOnSearchClick,
          },
        }),
        p({
          id: "board-search-failed-label",
          class: "text-danger",
        })
      )
    )
  );
}

function newFormModal(authHeader) {
  const id = "create-board";

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.srcElement); // Using the FormData object we can quickly fetch all the inputs
    const data = JSON.stringify(Object.fromEntries(formData.entries())); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    //TODO: Wrap in try .. catch

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards`, {
      method: "post",
      headers: { ...authHeader, "Content-Type": "application/json" },
      body: data,
    });

    //const result =
    await creationReq.json(); // Just await a response for now

    bootstrap.Modal.getInstance(document.querySelector(`#${id}-modal`)).hide(); // Hide the form modal

    window.dispatchEvent(new HashChangeEvent("hashchange")); // Assume everything went very much OK and do a soft refresh!

    // console.log(result.id);
  }

  return bootstrapGenerator.generateModal(
    id,
    generateFormModal(
      "board",
      handleOnSubmitCreate,
      [
        { type: "text", name: "Name", required: true },
        { type: "text", name: "Description", required: true },
      ],
      "Create"
    ),
    "Create a new board"
  );
}

export default {
  listing,
  details,
  boardCycle,
  searchBoard,
  newFormModal,
};
