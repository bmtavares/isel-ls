import cardGenerator from "./cardGenerator.js";
import { div, a, h3, h1 } from "./createElement.js";

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

function details(list, cards) {
  return div(h1(list.name), cardGenerator.listing(cards));
}

export default {
  listing,
  details,
};
