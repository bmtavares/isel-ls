import * as c from "./createElement.js";

function listingCard(list) {
  return c.div(
    { class: "card text-center" },
    c.div({ class: "card-body" }, c.h3(list.name, { class: "card-title" })),
    c.div(
      { class: "card-footer d-grid" },
      c.a("Details", {
        class: "btn btn-primary",
        href: `#boards/${list.boardId}/lists/${list.id}`,
      })
    )
  );
}

function listing(lists) {
  return c.div(
    { class: "row gy-2" },
    ...lists.map((list) => c.div({ class: "col-lg-4" }, listingCard(list)))
  );
}

export default {
  listing,
};
