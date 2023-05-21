import { div, a, h3, p, small } from "./createElement.js";
import userUtils from "../user.js";
const API_BASE_URL = "http://localhost:9000/";

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

function moveCard(card) {

  for (let i = 0; i < GlobalLists.length; i++) {
    listOptions += `<option value="${i}">${GlobalLists[i]}</option>`;
  }
  const question = "mmove this card?";
  const modalHtml = `
    <div class="modal fade" id="MoveCardModal" tabindex="-1" aria-labelledby="MoveCardModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="MoveCardModalLabel">Delete List</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>${question}</p>
            <select id="listDropdown">
                ${listOptions}
             </select>
             <select id="positionDropdown">
              <option value="0">Position 1</option>
              <option value="1">Position 2</option>
              <option value="2">Position 3</option>
              </select>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
            <button type="button" class="btn btn-primary" id="confirmMoveButton">Yes</button>
          </div>
        </div>
      </div>
    </div>`;

  // Add the modal HTML to the page
  document.body.insertAdjacentHTML('beforeend', modalHtml);

  // Add event listener to the confirm button
  const confirmButton = document.getElementById('confirmMoveButton');
    confirmButton.addEventListener('click', () => {
        const listDropdown = document.getElementById('listDropdown');
        const positionDropdown = document.getElementById('positionDropdown');
        const lid = listDropdown.value;
        const cix = positionDropdown.value;
      fetch(API_BASE_URL + 'boards/' + card.boardId + '/cards/' + card.id+"/move", {
        method: 'PUT',
        headers: userUtils.getAuthorizationHeader(),
        body: JSON.stringify({
        "lid": lid, //get from drop down
        "cix": cix //get from drop down
        })
      })
      .then(response => {
        console.log(response);
        if(response.ok){
        location.reload();
        }else{
        alert(`Move   error: ${response.statusText}`);
        location.reload();
        }
      })
      .catch(error => {
        console.log(error);
      });
    });


  // Show the modal
  const modal = new bootstrap.Modal(document.getElementById('MoveCardModal'), {
    keyboard: false,
    backdrop: 'static'
  });
  modal.show();
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
        ),
        a('Move', {
                class: 'btn btn-primary',
                events: {
                  click: () => { moveCard(card); }
                }
              })
    )
  );
}




export default {
  listing,
  details,
};
