const routes = []
let notFoundRouteHandler = () => { throw "Route handler for unknown routes not defined" }
/*
function addRouteHandler(path, handler){
    routes.push({path, handler})
}*/
function addRouteHandler(path, handler){
    // Replace variable parts of the path with regular expression groups
    const regexPath = path.replace(/\{(\w+)\}/g, "([^/]+)");

    // Create a regular expression to match the URL with the path
    const regex = new RegExp(`^${regexPath}$`);

    // Add the route to the routes array with the regular expression and handler
    routes.push({regex, handler});
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

function getRouteHandler(path){
    // Find the route that matches the current URL
    const route = routes.find(r => r.regex.test(path));

    if (route) {
        // Extract the variable parts of the URL as arguments to the route handler
        const args = path.match(route.regex).slice(1);

        // Call the route handler with the arguments and return the result
        return [route.handler,args];
    } else {
        // Call the notFoundRouteHandler if no route matches the URL
        return notFoundRouteHandler();
    }
}

const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler
}

export default router

