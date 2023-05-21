import { div, h1, h3, p, a } from "./createElement.js";
import listGenerator from "./listGenerator.js";
import { generateFormModal } from './formGenerator.js';
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
  return div(
    listGenerator.createFormModal(authHeader, board.id),
    h1(board.name),
    h3(board.description, { class: "text-muted" }),
    bootstrapGenerator.generateModalButton("Create new", "create-list-modal", "success"),
    listGenerator.listing(lists)
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
      headers: {...authHeader, "Content-Type": "application/json"},
      body: data
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
        { type: "text", name: "Name", required: true},
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
  newFormModal
};
