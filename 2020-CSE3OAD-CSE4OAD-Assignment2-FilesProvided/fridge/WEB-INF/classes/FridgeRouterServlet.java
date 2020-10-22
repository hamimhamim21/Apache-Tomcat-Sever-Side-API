import java.io.*;
import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;


public class FridgeRouterServlet extends HttpServlet {
    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARACTER_ENCODING = "utf-8";

    // http methods
    // These constants are also available at javax.ws.rs.HttpMethod
    // but it does not define PATCH, we we are creating our own.
    // more: https://www.restapitutorial.com/lessons/httpmethods.html
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";

    // status codes: http://www.informit.com/articles/article.aspx?p=29817&seqNum=7
    // more https://www.restapitutorial.com/lessons/httpmethods.html
    // we will be using those defined in HttpServletResponse
    // they are listed at https://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/http/HttpServletResponse.html
    // more: https://www.restapitutorial.com/httpstatuscodes.html

    public static final String CONTROLLER_STR = "Controller";


    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO 14: Set the response CONTENT_TYPE and CHARACTER_ENCODING
		response.setContentType(CONTENT_TYPE);
		response.setContentType(CHARACTER_ENCODING);
        Object responseObj = null;

        // TODO 15: Get the path info from the HttpServletRequest argument. We need to get the URL path from incoming request
        //String pathInfo = ""; // <-- some changes needed here
		String pathInfo = request.getPathInfo();	
        // TODO 16: Get the http method from the HttpServletRequest argument. We need to get incoming request method
        //String httpMethod = ""; // <-- some changes needed here
		String httpMethod = request.getMethod();
        // pathInfo will be in format: /{resource-name}/{query-string}
        // we want resource-name; we split on "/" and take the
        // second occurence, which is array position 1 of split("/");
        // any third occurence would be a model id or a search query parameter
        String pathInfoArray[] = pathInfo.split("/");

        try {
            // pathInfo has to have at least a resource-name, which is at array
            // location 1 in pathInfoArray - if no resource-name found throws error
            if (pathInfoArray.length <= 1) {
                throw new MissingArgumentException("Resource target not defined.");
            }
            // TODO 17: Get the data model name from pathInfoArray. The model name will be in the request after /api/
            //String modelName = ""; // <-- some changes needed here
            String modelName = pathInfoArray[1];
            // Setting the data model to be uppercase and all other characters lowercase 
            modelName = modelName.toLowerCase();
            modelName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1);
            
            // the controller is needed for the matching action defined by the http method
            String controllerName = String.join("", modelName, CONTROLLER_STR);

            // we then use Java Reflection to find Model and Controller,
            //      example: if resource-name is "groceRy", matching
            //      model class is Grocery and matching controller class
            //      is GroceryController

            // TODO 18: Find the controllerClass using String controllerName,
            //Class<?> controllerClass = null; // <-- some changes needed here, hint: Class.forName(...)
			Class<?> controllerClass = Class.forName(controllerName);
			
            // TODO 19: Find the modelClass using String modelName
            //Class<?> modelClass = null; // <-- some changes needed here, hint: Class.forName(...)
			Class<?> modelClass = Class.forName(modelName);
            // getting database config info from web.xml, putting the info in
            // a string array
            String[] dbConfig = new String[3];
            dbConfig[0] = getServletContext().getInitParameter("dbhost");
            dbConfig[1] = getServletContext().getInitParameter("dbusername");
            dbConfig[2] = getServletContext().getInitParameter("dbpassword");

            // creating an instance of controllerClass; NOTE that the next 2 lines finds
            // the matching constructor (with 3 String arguments) and instantiate the
            // class using that constructor passing in String array dbConfig of length 3
            Constructor constructor = controllerClass.getConstructor(
                new Class[] {String.class, String.class, String.class}
            );

            Object controllerInstance = constructor.newInstance((Object[]) dbConfig);

            int modelId = 0;
            Method method = null;
            
            switch (httpMethod) {
                case HTTP_GET:
                    // Any 3rd argument for an HTTP GET(pathInfoArray) is either an "e" or it is the
                    // relevant model id. If there is a third element then find the matching controllerClass method get(int id)
                    // NOTE: the 3rd argument is sent as a String and needs to be
                    // parsed as our model id is of type int.
                    
                    if (pathInfoArray.length >= 3) {
                        if( pathInfoArray[2].equals("e")) { // the 3rd argument is an "e"
                            method = controllerClass.getMethod("getAllExpiredItems");
                            // Both GroceryController and ItemController have a getAllExpiredItems() method
                            responseObj = method.invoke(controllerInstance);
                        } else {
                        // TODO 20: find the modelId in pathInfoArray (don't forget to parse to int)
                        // Your grocery id will be 3rd element in the array
                            //modelId = 0; // <-- some changes needed here
							modelId = Integer.parseInt(pathInfoArray[2]);
                            
							method = controllerClass.getMethod("get", int.class);
                            responseObj = method.invoke(controllerInstance, modelId);

                            if (responseObj == null) {
                                throw new ResourceNotFoundException(modelName + " with id " + modelId + " not found!");
                            }
                        }
                    }
                    // else find the matching controllerClass method get()
                    else {

                        // TODO 21: identify method get with no id. HINT look above
                        //method = null; // <-- some changes needed here
						method = controllerClass.getMethod("get");
                        //TODO 22: invoke method on controllerInstance. HINT look above
                        //responseObj = null; // <-- some changes needed here
						responseObj = method.invoke(controllerInstance);
                    }
                    break;
                case HTTP_POST: // NOTE: this case is given fully complete; it is the most complex part; use it as a reference example
                    // grab post data
                    String resourceData = buildResourceData(request);

                    // find relevant add method in controllerClass
                    method = controllerClass.getMethod("add", modelClass);

                    // invoke method with parse (converted from JSON to modelClass) post data
                    Object id = method.invoke(controllerInstance, new Gson().fromJson(resourceData, modelClass));

                    Map<String, String> message = new HashMap<String, String>();
                    message.put("message", "created ok! " + modelName + " id " + Integer.parseInt(id.toString()));
                    responseObj = message;
                    break;
                case HTTP_PUT:
                    if (pathInfoArray.length >= 3) {

                        // TODO 23: Find the modelId in pathInfoArray (don't forget to parse to int)
                        //modelId = 0; // <-- some changes needed here
						modelId = Integer.parseInt(pathInfoArray[2]);
                        // TODO 24: Identify method update with id
                        //method = null; // <-- some changes needed here
						method = controllerClass.getMethod("update", int.class);
                         

                        //TODO 25: Invoke method on controllerInstance, passing modelId
                        //responseObj = null; // <-- some changes needed here
						responseObj = method.invoke(controllerInstance, modelId);
						 
                        if (responseObj == null)
                            throw new ResourceNotFoundException(modelName + " with id " + modelId + " not found! Cannot Update!");
                    } else
                        throw new MissingArgumentException("Attribute id required! Cannot Update!");
                    break;
                case HTTP_DELETE:
                    if (pathInfoArray.length >= 3) {

                        // TODO 26: find the modelId in pathInfoArray (don't forget to parse to int)
                        //modelId = 0; // <-- some changes needed here
						modelId = Integer.parseInt(pathInfoArray[2]);

                        // TODO 27: identify method get with id
                        //method = null; // <-- some changes needed here
						method = controllerClass.getMethod("delete", int.class);

                        //TODO 28: invoke method on controllerInstance, passing modelId
                        //responseObj = null; // <-- some changes needed here
						responseObj = method.invoke(controllerInstance, modelId);

                        if (Integer.parseInt(responseObj.toString()) <= 0)
                            throw new ResourceNotFoundException(modelName + " with id " + modelId + " not found! Cannot Delete!");

                        responseObj = buildMessage(modelName + " with id " + modelId + " deleted!");
                    } else
                        throw new MissingArgumentException("Attribute id required! Cannot Delete!");
                    break;
                default:
                    // we do not provide action for any other HTTP methods
                    throw new NoSuchMethodException();
            } // switch

            response.getWriter().print(new Gson().toJson(responseObj));
        }
        catch (Exception exp) {
            String message = exp.getMessage();
            // setting default error status code
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);

            if (exp instanceof ResourceNotFoundException)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            if (exp instanceof MissingArgumentException)
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            if (exp instanceof ClassNotFoundException) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                message = "Resource not found!";
            }

            if (exp instanceof NoSuchMethodException) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                message = "Method not allowed on specified resource!";
            }

            if (exp instanceof InvocationTargetException) {
					if(exp.getCause() instanceof UpdateNotAllowedException)
					{
						response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
						message = exp.getCause().getMessage();
					}
					

                // TODO 30: identify instanceof ValidationException exception
                    // set response status to SC_PRECONDITION_FAILED
                    // set message to intercepted exception
                    // see provided Validation Framework Validator class (exception handling part) for how to
                    //  - this was covered in Labs
					if(exp.getCause() instanceof ValidationException)
					{
						response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
						message = exp.getCause().getMessage();
					}

            }

            response.getWriter().write(new Gson().toJson(buildMessage(message)));
        }

    }


    // HELPER METHODS
    private String buildResourceData(HttpServletRequest request) throws Exception {
        // request has post/put data
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    private Map buildMessage(String msg) {
        Map<String, String> message = new HashMap<String, String>();
        message.put("message", msg);
        return message;
    }

}
