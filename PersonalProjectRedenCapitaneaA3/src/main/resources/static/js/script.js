function verify() {
	var password1 = document.forms['regForm']['password'].value;
	var password2 = document.forms['regForm']['verifyPassword'].value;
	if (password1 == null || password1 == "" || password1 != password2) {
		document.getElementById("error").innerHTML = "Please check your passwords";
		return false;
	}
	if (password1.length < 8) {
		document.getElementById("error").innerHTML = "Password length must be greater 7";
		return false;
	}
	
	if (password1.search(/\d/) == -1) {
		document.getElementById("error").innerHTML = "Password must contain a number";
		return false;
	}
	
	if (password1.search(/[a-zA-Z]/) == -1) {
		document.getElementById("error").innerHTML = "Password must contain a letter";
		return false;	
	}
}