import router from "./router.js";
import handlers from "./handlers.js";


window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){
    router.addRouteHandler("home", handlers.getHome)
    router.addRouteHandler("userDetails", handlers.getUser)
    router.addRouteHandler("boards", handlers.getBoards)
    router.addRouteHandler("boards/{id}", handlers.getBoardDetail)
    router.addRouteHandler("board/{id}/user-list", handlers.getBoardsUsers)
    router.addRouteHandler("boards/{id}/lists/{id}", handlers.listDetails)
    router.addRouteHandler("boards/{id}/lists/{id}/cards/{id}", handlers.cardDetail)
    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")
    hashChangeHandler()
}

function hashChangeHandler(){

    const mainContent = document.getElementById("mainContent")

    const path =  window.location.hash.replace("#", "")
    const resp = router.getRouteHandler(path)
    const handler = resp[0]
    const args = resp[1]
    handler(mainContent,args)
}