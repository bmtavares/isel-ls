import * as c from "./createElement.js";

function listingCard(card) {
  return c.div(
    { class: "card text-center" },
    c.div({ class: "card-body" }, c.h3(card.name)),
    c.div(
      { class: "card-footer d-grid" },
      c.a("Details", {
        class: "btn btn-primary",
        href: `#boards/${card.boardId}/lists/${card.listId}/cards/${card.id}`,
      })
    )
  );
}

function listing(cards) {
  return c.div(
    { class: "row gy-2" },
    ...cards.map((card) => c.div({ class: "col-lg-3" }, listingCard(card)))
  );
}

function details(card) {
  return c.div(
    c.h1(card.name),
    cardGenerator.listing(cards)
  );
}

export default {
  listing,
  details
};
