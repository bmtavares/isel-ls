import { div, h3, p } from "./createElement.js";

function listingCard(user) {
  return div(
    {
      class: "card",
    },
    div(
      {
        class: "card-body",
      },
      h3(`${user.name}`, {
        class: "card-title",
      }),
      p(`✉ ${user.email}`, {
        class: "card-text",
      })
    )
  );
}

function listing(users) {
  return div(
    { class: "row gy-2" },
    ...users.map((user) => div({ class: "col-lg-3" }, listingCard(user)))
  );
}

export default {
  listingCard,
  listing,
};
