import cardGenerator from "./cardGenerator.js";
import { div, a, h3, h1 } from "./createElement.js";
import userUtils from "../user.js";

const API_BASE_URL = "http://localhost:9000/";


function listDelete(list) {
        const question =`Do you want to delete list: ${list.name} ?`
        const response = confirm(question);
        if (response == true) {
              fetch(API_BASE_URL + "boards/" + list.boardId + "/lists/" + list.id, {
                method: 'DELETE',
                headers: userUtils.getAuthorizationHeader(),
              })


          location.reload()
        } else {
          console.log("User does not want to delete the list.");
        }
      }


function listingCard(list) {
  return div(
    { class: "card text-center" },
    div({ class: "card-body" }, h3(list.name, { class: "card-title" })),
    div(
      { class: "card-footer d-grid" },
      a("Details", {
        class: "btn btn-primary",
        href: `#boards/${list.boardId}/lists/${list.id}`,
      }),
      a("Delete", {
        class: "btn btn-primary",
                  events: {
                    click: () => {listDelete(list)}
                  }
        //onclick:  "listDelete()", // call the function and return its result
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
