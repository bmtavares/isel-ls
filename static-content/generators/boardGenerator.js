import {div, h1, h3, p, a, button} from "./createElement.js";
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
  return div(
    h1(board.name),
    h3(board.description, { class: "text-muted" }),
    listGenerator.listing(lists)
  );
}
function boardCycle(){
  let boards =  JSON.parse(localStorage.getItem("searchBoardsResult"))
  let idx = localStorage.getItem("boardIdx")
  if (idx === null){
    idx = 0
    localStorage.setItem("boardIdx",idx.toString())
  }else {
    idx = Number(idx)
  }
  let board = boards[idx]
  let prev;
  let next;
  if(idx-1<0){ prev = boards.length-1} else prev = idx-1
  if(idx+1>=boards.length){ next = 0} else next = idx+1
  return  div( {class: "container mt-4"},
      div(
          {class: "d-flex justify-content-between"},
          div(
              button("<<Previous", { class: "btn btn-link",events:{
                  click:async (e) => {
                    e.preventDefault()
                    localStorage.setItem("boardIdx",prev.toString())
                    window.location.reload()
                  } } } )
          ),
          div(
              button("Next>>", { class: "btn btn-link" ,events:{
                  click:async (e) => {
                    e.preventDefault()
                    localStorage.setItem("boardIdx",next.toString())
                    window.location.reload()
                  } } })
          )
      ),
      div(
          { class: "card text-center" },
          div(
              { class: "card-header d-grid" },
              h3(board.name, { class: "card-title" }),
          ),
          div(
              { class: "card-body" },
              p(board.description, { class: "card-text" })
          ),
          div(
              { class: "card-footer" },
              div(
                  { class: 'd-flex justify-content-between' },
                  a("View", { class: "btn btn-info",style:"width: 100px", href: `#boards/${board.id}` }),
                  a("Edit", { class: "btn btn-primary",style:"width: 100px", href: `#boards/${board.id}` }),
                  a("+ Add", { class: "btn btn-success",style:"width: 100px", href: `#boards` }),
                  a("Delete", { class: "btn btn-danger",style:"width: 100px", href: `#boards/${board.id}` }),
              )
          )
      ))
}




export default {
  listing,
  details,
  boardCycle
};
