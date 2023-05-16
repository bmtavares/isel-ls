import cardGenerator from "./cardGenerator.js";
import { div, a, h3, h1 } from "./createElement.js";
import userUtils from "../user.js";

const API_BASE_URL = "http://localhost:9000/";

function listingCard(list) {
  return div(
    { class: 'card text-center' },
    div({ class: 'card-body' }, h3(list.name, { class: 'card-title' })),
    div(
      { class: 'card-footer d-grid' },
      a('Details', {
        class: 'btn btn-primary',
        href: `#boards/${list.boardId}/lists/${list.id}`,
      }),
      a('Delete', {
        class: 'btn btn-primary',
        events: {
          click: () => { listDelete(list); }
        }
      })
    )
  );
}


function listDelete(list) {
  const question = `Do you want to delete list: ${list.name} ?`;
  const modalHtml = `
    <div class="modal fade" id="deleteListModal" tabindex="-1" aria-labelledby="deleteListModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="deleteListModalLabel">Delete List</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>${question}</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-primary" id="confirmDeleteListButton">Delete</button>
          </div>
        </div>
      </div>
    </div>`;

  // Add the modal HTML to the page
  document.body.insertAdjacentHTML('beforeend', modalHtml);

  // Add event listener to the confirm button
  const confirmButton = document.getElementById('confirmDeleteListButton');
    confirmButton.addEventListener('click', () => {
      fetch(API_BASE_URL + 'boards/' + list.boardId + '/lists/' + list.id, {
        method: 'DELETE',
        headers: userUtils.getAuthorizationHeader(),
      })
      .then(response => {
        console.log(response);
        if(response.ok){
        location.reload();
        }else{
        alert(`Delete list failed   error: ${response.statusText}`);
        location.reload();
        }
      })
      .catch(error => {
        console.log(error);
      });
    });


  // Show the modal
  const modal = new bootstrap.Modal(document.getElementById('deleteListModal'), {
    keyboard: false,
    backdrop: 'static'
  });
  modal.show();
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
