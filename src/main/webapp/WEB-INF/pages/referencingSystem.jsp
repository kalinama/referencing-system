<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Referencing System">
    <div class="card-body">
    <div class="d-flex flex-row">

        <div class="p-2" style="min-width: 570px"></div>
        <div class="p-2  border rounded border-dark ">
            <h4>Upload files for referencing:</h4>
            <form action="${pageContext.request.contextPath}/reference" method="post" autocomplete="off"
                  enctype="multipart/form-data" id="docForm">
                <div class="form-group attachment">
                    <input type="file" name="files" multiple/>
                </div>
                <button class="btn" type="submit" style="background: #e44c4c; ">
                    Handle
                </button>
            </form>
        </div>
    </div>

    <c:if test="${result ne null}">
        <div class="d-flex flex-row">
            <div class="d-flex flex-column">

                <div class="p-2" style="font-weight: bold">
                    Classical:
                </div>
                <div class="p-2 border rounded border-dark" style="background-color: fff6f7; min-width: 700px">
                        ${result.snippet}
                </div>
                <div class="p-2" style="font-weight: bold">
                    ML:
                </div>
                <div class="p-2 border rounded border-dark" style="background-color: fff6f7;  min-width: 700px">

                        ${result.snippetML}
                </div>
            </div>
            <div class="d-flex flex-column">
                <div class="p-2" style="min-width: 35px"></div>
            </div>

            <div class="d-flex flex-column">
                <div class="p-2" style="font-weight: bold">
                    Document content:
                </div>
                <div class="p-2 border rounded border-dark" style="background-color: #ddf3fc">

                        ${result.documentContent}
                </div>
            </div>
        </div>
        </div>
    </c:if>
    </div>

</tags:master>

