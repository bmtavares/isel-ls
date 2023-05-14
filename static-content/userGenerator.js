import * as c from "./createElement.js";

function listingCard(user) {
  return c.div(
    {
      class: "card",
    },
    c.div(
      {
        class: "card-body",
      },
      c.h3(`${user.name}`, {
        class: "card-title",
      }),
      c.p(`✉ ${user.email}`, {
        class: "card-text",
      })
    )
  );
}

function listing(users) {
  return c.div(
    { class: "row gy-2" },
    ...users.map((user) => c.div({ class: "col-lg-3" }, listingCard(user)))
  );
}

export default {
  listingCard,
  listing,
};
