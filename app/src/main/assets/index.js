function initPaypal(amount,userId){
    paypal.Buttons({
        createOrder: function(data,actions){
            return actions.order.create({
                purchase_units: [{
                amount:{
                    value: amount
                }
            }]
            });
        },
        onApprove: function(data, actions){
            return actions.order.capture().then(function(details){
                console.log(details.status)
                if(details.status === "COMPLETED"){
                    updateDatabase(amount,userId);
                }
            })
        }
    }).render('#paypal-container')
}

function getUrlParam(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const pay = urlParams.get('pay')
    const userId = urlParams.get('userId')
    console.log(pay+" " + userId)
    paramObj = {"pay" : pay , "userId":userId}
    return paramObj

    //console.log(urlParams.get('pay'))
}
function initFirebase(){
    var firebaseConfig = {
        apiKey: "AIzaSyAF80iCoabvhCrNKjLtKa5hUIGW8BAkxqc",
        authDomain: "playersgameapp.firebaseapp.com",
        databaseURL: "https://playersgameapp.firebaseio.com",
        projectId: "playersgameapp",
        storageBucket: "playersgameapp.appspot.com",
        messagingSenderId: "1030833582509",
        appId: "1:1030833582509:web:ebbf4c53568a0c20e47124",
        measurementId: "G-6DQFXN2QDS"
      };
      firebase.initializeApp(firebaseConfig);
      firebase.analytics();
}
function updateDatabase(amount,userID){
    var users = firebase.database().ref("users/" + userID);
    var balance;
    users.once('value', (snapshot) => {
        balance = snapshot.child("account_balance").val()
        console.log(balance)
        if(balance!= null && balance>-1){
            var bal = parseFloat(balance) + parseFloat(amount)
            console.log(bal)
            if(bal != null && bal != NaN){
                users.update({
                    account_balance:bal
                })
            }
        }

    })



}
param = getUrlParam()
initFirebase()
initPaypal(param.pay,param.userId)







