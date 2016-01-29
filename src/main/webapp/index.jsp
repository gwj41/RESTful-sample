<html>
<head>
    <meta >
    <title>RESTful form</title>
</head>
<body>
<h2>@FormParam</h2>
<FORM action="service/info/customerservice/customers/form" method="post">
    <P>
        First name: <INPUT type="text" name="firstName"><BR>
        Last name: <INPUT type="text" name="lastName"><BR>
        <INPUT type="submit" value="Send">
    </P>
</FORM>
<h2>@BeanParam</h2>
<FORM action="service/info/customerservice/customers/form/bean" method="post">
    <P>
        First name: <INPUT type="text" name="firstName"><BR>
        Last name: <INPUT type="text" name="lastName"><BR>
        <INPUT type="submit" value="Send">
    </P>
</FORM>
<h2>MultiValuedMap</h2>
<FORM action="service/info/customerservice/customers/multiValuedMap" method="post">
    <P>
        First name: <INPUT type="text" name="firstName"><BR>
        Last name: <INPUT type="text" name="lastName"><BR>
        <INPUT type="submit" value="Send">
    </P>
</FORM>
<a href="service/info/customerservice/singleCustomer">Get single customer with Message Body Writer</a>
</body>
</html>
