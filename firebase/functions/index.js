const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.updateContacts = functions.https.onRequest((request, response) => {
  if (request.method !== "POST") {
    response.send(405, 'HTTP Method ' + request.method + ' not allowed');
    return;
  }
  const contacts = request.body.contacts;
  if (contacts === undefined) {
    response.send(400, 'Bad request');
    return;
  }
  const promises = [];
  contacts.forEach((uid, i) => {
    promises.push(admin.firestore().collection('users').doc(uid).get());
  })
  Promise.all(promises).then((userResults) => {
    const users = [];
    userResults.forEach((userResult, i) => {
      if (userResult.exists) {
        users.push(userResult.id)
      }
    })
    response.type('application/json')
    response.send({ 'data': users });
    return;
  }).catch((e) => {
    response.type('application/json')
    response.status(500);
    response.send(e);
  })
})

exports.sendPlayRequestNotfication = functions.firestore.document("users/{user_id}/play_request_received/{sent_user_id}").onWrite((data, context) => {
  const user_id = context.params.user_id;
  const sent_user_id = context.params.sent_user_id;
  console.log('Sending notification to ', user_id);

  if (data.after.exists) {
    sendPlayRequestNotification(user_id, sent_user_id);
  } else {
    sendPlayRequestAcceptedNotification(sent_user_id, user_id);
  }
});

function sendPlayRequestNotification(user_id, sent_user_id) {
  admin.auth().getUserByPhoneNumber(sent_user_id).then((sentUserProfile) => {
    console.log('Fetched user profile', { id: sentUserProfile.phoneNumber, name: sentUserProfile.name });

    const displayName = sentUserProfile.displayName || sent_user_id;

    const payload = {
      data: {
        title: 'You have a new Play Request!',
        body: `${displayName} sent you Play Request`,
        userId: sent_user_id,
        name: `${displayName}`,
        photoUrl: `${sentUserProfile.photoURL}`,
        action: 'play_request',
      }
    };

    return admin.messaging().sendToTopic(user_id.replace('+', '%'), payload);
  }).catch(e => {
    console.log(e);
  });
}

function sendPlayRequestAcceptedNotification(user_id, sent_user_id) {
  admin.auth().getUserByPhoneNumber(sent_user_id).then((sentUserProfile) => {
    console.log('Fetched user profile', { id: sentUserProfile.phoneNumber, name: sentUserProfile.name });

    const displayName = sentUserProfile.displayName || sent_user_id;

    const payload = {
      data: {
        title: 'Play Request Accepted!',
        body: `${displayName} accepted your Play Request`,
        userId: sent_user_id,
        name: `${displayName}`,
        photoUrl: `${sentUserProfile.photoURL}`,
        action: 'play_request_accepted',
      }
    };

    return admin.messaging().sendToTopic(user_id.replace('+', '%'), payload);
  }).catch(e => {
    console.log(e);
  });
}