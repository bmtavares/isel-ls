import cardGenerator from "./cardGenerator.js";
import { div, a, h3, h1 } from "./createElement.js";
import { generateFormModal } from "./formGenerator.js";
import bootstrapGenerator from "./bootstrapGenerator.js";
import appConstants from "../appConstants.js";

function listingCard(list) {
  return div(
    { class: "card text-center" },
    div({ class: "card-body" }, h3(list.name, { class: "card-title" })),
    div(
      { class: "card-footer d-grid" },
      a("Details", {
        class: "btn btn-primary",
        href: `#boards/${list.boardId}/lists/${list.id}`,
      })
    )
  );
}

function listing(lists) {
  return div(
    { class: "row gy-2" },
    ...lists.map((list) => div({ class: "col-lg-4" }, listingCard(list)))
  );
}

function details(boardId, list, cards, authHeader) {
  return div(
    cardGenerator.createFormModal(authHeader, boardId, list.id),
    h1(list.name),
    bootstrapGenerator.generateModalButton("Create new", "create-card-modal", "success"),
    cardGenerator.listing(cards)
    );
}

function createFormModal(authHeader, boardId) {
  const id = "create-list";

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.srcElement); // Using the FormData object we can quickly fetch all the inputs
    const data = JSON.stringify(Object.fromEntries(formData.entries())); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    //TODO: Wrap in try .. catch

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards/${boardId}/lists`, {
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
      "list",
      handleOnSubmitCreate,
      [
        { type: "text", name: "Name", required: true }
      ],
      "Create"
    ),
    "Create a new list"
  );
}

export default {
  listing,
  details,
  createFormModal,
};
