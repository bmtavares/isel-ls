import cardGenerator from "./cardGenerator.js";
import { div, a, h3, h1,h5,p,button } from "./createElement.js";
import userUtils from "../user.js";
import { generateFormModal } from "./formGenerator.js";
import bootstrapGenerator from "./bootstrapGenerator.js";
import appConstants from "../appConstants.js";

function listingCard(list) {
  return div(
      { class: 'card text-center' },
      div({ class: 'card-body' }, h3(list.name, { class: 'card-title' })),
      div(
          { class: 'card-footer' },
          div(
              { class: 'd-flex justify-content-between' },
              a('Details', {
                class: 'btn btn-primary',
                href: `#boards/${list.boardId}/lists/${list.id}`,
              }),
              a('Delete', {
                class: 'btn btn-danger',
                events: {
                  click: () => { listDelete(list); }
                }
              })
          )
      ));
}


function modalListDeleteGenericDOM(list){
const question = `Do you want to delete list: ${list.name} ?`;
const modal = document.createElement('div');
        modal.classList.add('modal', 'fade');
        modal.id = 'deleteListModal';
        modal.setAttribute('tabindex', '-1');
        modal.setAttribute('aria-labelledby', 'deleteListModalLabel');
        modal.setAttribute('aria-hidden', 'true');

        const modalDialog = document.createElement('div');
        modalDialog.classList.add('modal-dialog');

        const modalContent = document.createElement('div');
        modalContent.classList.add('modal-content');

        const modalHeader = document.createElement('div');
        modalHeader.classList.add('modal-header');

        const modalTitle = document.createElement('h5');
        modalTitle.classList.add('modal-title');
        modalTitle.id = 'deleteListModalLabel';
        modalTitle.textContent = 'Delete List';

        const closeButton = document.createElement('button');
        closeButton.type = 'button';
        closeButton.classList.add('btn-close');
        closeButton.setAttribute('data-bs-dismiss', 'modal');
        closeButton.setAttribute('aria-label', 'Close');

        const modalBody = document.createElement('div');
        modalBody.classList.add('modal-body');

        const paragraph = document.createElement('p');
        paragraph.textContent = question;

        const modalFooter = document.createElement('div');
        modalFooter.classList.add('modal-footer');

        const cancelButton = document.createElement('button');
        cancelButton.type = 'button';
        cancelButton.classList.add('btn', 'btn-secondary');
        cancelButton.setAttribute('data-bs-dismiss', 'modal');
        cancelButton.textContent = 'Cancel';
        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.classList.add('btn', 'btn-primary');
        deleteButton.id = 'confirmDeleteListButton';
        deleteButton.textContent = 'Delete';

        modalHeader.appendChild(modalTitle);
        modalHeader.appendChild(closeButton);
        modalBody.appendChild(paragraph);
        modalFooter.appendChild(cancelButton);
        modalFooter.appendChild(deleteButton);
        modalContent.appendChild(modalHeader);
        modalContent.appendChild(modalBody);
        modalContent.appendChild(modalFooter);
        modalDialog.appendChild(modalContent);

        modal.appendChild(modalDialog);
        document.body.appendChild(modal);

}

function modalListDelete(list){
/*
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
    */
const question = `Do you want to delete list: ${list.name} ?`;
 const content = div(
          { class: "modal fade",
           id: "deleteListModal",
           tabindex: "-1",
           "aria-labelledby": "deleteListModalLabel",
            "aria-hidden": "true"
           },
          div(
          {class:"modal-dialog"},
            div({class:"modal-content"},
                div({class:"modal-header"},
                    h5("Delete List",{class:"modal-title",
                        id:"deleteListModalLabel"
                        }
                    ),
                    button({ type:"button", class:"btn-close" , "data-bs-dismiss":"modal", "aria-label":"Close" }
                    )
                ),
                div({class:"modal-body"},
                    p(question)
                ),
                div({class:"modal-footer"},
                    button("Cancel",{type:"button", class:"btn btn-secondary", "data-bs-dismiss":"modal"}),
                    button("Delete",{type:"button", class:"btn btn-primary", id:"confirmDeleteListButton"})
                )
            )
          )
        );
document.body.appendChild(content);
}


function listDelete(list) {
        modalListDelete(list);


  // Add event listener to the confirm button
  const confirmButton = document.getElementById('confirmDeleteListButton');
  confirmButton.addEventListener('click', () => {
    fetch(appConstants.API_BASE_URL + 'boards/' + list.boardId + '/lists/' + list.id, {
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
  const modall = new bootstrap.Modal(document.getElementById('deleteListModal'), {
    keyboard: false,
    backdrop: 'static'
  });
  modall.show();
}





function listing(lists) {
  return div(
      { class: "row gy-2" },
      ...lists.map((list) => div({ class: "col-lg-4" }, listingCard(list)))
  );
}

function details(boardId, list, cards, authHeader) {
  return div(
    cardGenerator.createFormModal(authHeader, boardId, list.id),
    h1(list.name),
    bootstrapGenerator.generateModalButton("Create new", "create-card-modal", "success"),
    cardGenerator.listing(cards)
    );
}

function createFormModal(authHeader, boardId) {
  const id = "create-list";

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.srcElement); // Using the FormData object we can quickly fetch all the inputs
    const data = JSON.stringify(Object.fromEntries(formData.entries())); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    //TODO: Wrap in try .. catch

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards/${boardId}/lists`, {
      method: "post",
      headers: { ...authHeader, "Content-Type": "application/json" },
      body: data,
    });

    //const result =
    await creationReq.json(); // Just await a response for now

    bootstrap.Modal.getInstance(document.querySelector(`#${id}-modal`)).hide(); // Hide the form modal

    window.dispatchEvent(new HashChangeEvent("hashchange")); // Assume everything went very much OK and do a soft refresh!

    // console.log(result.id);
  }

  return bootstrapGenerator.generateModal(
    id,
    generateFormModal(
      "list",
      handleOnSubmitCreate,
      [
        { type: "text", name: "Name", required: true }
      ],
      "Create"
    ),
    "Create a new list"
  );
}

export default {
  listing,
  details,
  createFormModal,
};