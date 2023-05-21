import { div, h1, h3, p, a } from "./createElement.js";
import listGenerator from "./listGenerator.js";

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

function details(board, lists) {
 localStorage.setItem("GlobalLists",JSON.stringify(lists))
  return div(
    h1(board.name),
    h3(board.description, { class: "text-muted" }),
    listGenerator.listing(lists)
  );
}

export default {
  listing,
  details,
};
