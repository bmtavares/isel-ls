import { div, a, h3, p, small } from "./createElement.js";
import bootstrapGenerator from "./bootstrapGenerator.js";
import { generateFormModal } from "./formGenerator.js";
import appConstants from "../appConstants.js";

function listingCard(card) {
  return div(
    { class: "card text-center" },
    div({ class: "card-body" }, h3(card.name, { class: "card-title" })),
    div(
      { class: "card-footer d-grid" },
      a("Details", {
        class: "btn btn-primary",
        href: `#boards/${card.boardId}/lists/${card.listId}/cards/${card.id}`,
      })
    )
  );
}

function listing(cards) {
  return div(
    { class: "row gy-2" },
    ...cards.map((card) => div({ class: "col-lg-3" }, listingCard(card)))
  );
}

function details(card) {
  return div(
    { class: "card text-center" },
    div(
      { class: "card-body" },
      h3(card.name, { class: "card-title" }),
      p(card.description, { class: "card-text" }),
      card.dueDate &&
        p(
          { class: "card-text" },
          small(`📅 ${new Date(card.dueDate).toLocaleString()}`, {
            class: "text-body-secondary",
          })
        )
    )
  );
}

function createFormModal(authHeader, boardId, listId) {
  const id = "create-card";

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.srcElement); // Using the FormData object we can quickly fetch all the inputs

    const formEntries = Object.fromEntries(formData.entries());
    formEntries.dueDate = formEntries.dueDate ? new Date(`${formEntries.dueDate}+00:00`).getTime() : null; // Convert to Unix Timestamp as UTC

    const data = JSON.stringify(formEntries); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    //TODO: Wrap in try .. catch


    console.log(data);

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards/${boardId}/lists/${listId}/cards`, {
      method: "post",
      headers: { ...authHeader, "Content-Type": "application/json" },
      body: data,
    });

    //const result =
    await creationReq.json(); // Just await a response for now

    bootstrap.Modal.getInstance(document.querySelector(`#${id}-modal`)).hide(); // Hide the form modal

    window.dispatchEvent(new HashChangeEvent("hashchange")); // Assume everything went very much OK and do a soft refresh!

    console.log(result.id);
  }

  return bootstrapGenerator.generateModal(
    id,
    generateFormModal(
      "card",
      handleOnSubmitCreate,
      [
        { type: "text", name: "Name", required: true },
        { type: "text", name: "Description", required: true },
        { type: "datetime-local", name: "dueDate", label: "Due date" }
      ],
      "Create"
    ),
    "Create a new card"
  );
}

export default {
  listing,
  details,
  createFormModal
};
