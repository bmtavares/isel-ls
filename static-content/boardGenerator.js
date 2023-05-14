import * as c from "./createElement.js";
import listGenerator from "./listGenerator.js";

function listingCard(board) {
  return c.div(
    { class: "card text-center" },
    c.div(
      { class: "card-body" },
      c.h3(board.name, { class: "card-title" }),
      c.p(board.description, { class: "card-text" })
    ),
    c.div(
      { class: "card-footer d-grid" },
      c.a("View", { class: "btn btn-primary", href: `#boards/${board.id}` })
    )
  );
}

function listing(boards) {
  return c.div(
    { class: "row gy-2" },
    ...boards.map((board) => c.div({ class: "col-lg-4" }, listingCard(board)))
  );
}

function details(board, lists) {
  return c.div(
    c.h1(board.name),
    c.h3(board.description, { class: "text-muted" }),
    listGenerator.listing(lists)
  );
}

export default {
  listing,
  details,
};
