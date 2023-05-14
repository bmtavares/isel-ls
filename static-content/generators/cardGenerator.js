import { div, a, h3, p, small } from "./createElement.js";

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

export default {
  listing,
  details,
};
