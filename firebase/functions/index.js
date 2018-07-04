const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const db = admin.firestore();

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
    promises.push(db.collection('users').doc(uid).get());
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

exports.searchUser = functions.https.onRequest((request, response) => {
  const searchName = request.query.name;
  const userId = request.query.userid;
  const friendRequestSentPromise = db.collection(`users/${userId}/friend_request_sent`).get();
  const friendRequestReceivedPromise = db.collection(`users/${userId}/friend_request_received`).get();
  const searchUserPromise = db.collection('users/')
    .get();

  Promise.all([searchUserPromise, friendRequestSentPromise, friendRequestReceivedPromise]).then((results) => {
    console.log('No of Users', results[0].docs.length)

    const requestSentIds = [];
    results[1].docs.forEach((doc, index) => {
      requestSentIds.push(doc.id);
    })
    console.log('Request Sent ', requestSentIds);

    const requestReceivedIds = [];
    results[2].docs.forEach((doc, index) => {
      requestReceivedIds.push(doc.id);
    })
    console.log('Request Received', requestReceivedIds);

    const users = []
    results[0].docs.forEach((user, index) => {
      const userData = user.data();
      if (String(userData.name).toLowerCase().includes(String(searchName).toLowerCase()) && user.id !== userId) {
        if (requestSentIds.includes(user.id))
          users.push({ name: userData.name, id: user.id, status: 2 })
        else if (requestReceivedIds.includes(user.id))
          users.push({ name: userData.name, id: user.id, status: 3 })
        else
          users.push({ name: userData.name, id: user.id, status: 0 })
      }
    })
    response.type('application/json')
    response.send({ data: users });
    return;
  }).catch((r) => {
    console.log('Catch : ', r)
  });
})

exports.sendFriendRequestNotfication = functions.firestore.document("users/{user_id}/friend_request_received/{sent_user_id}").onWrite((event) => {
  const user_id = event.params.user_id;
  const sent_user_id = event.params.sent_user_id;
  console.log('Sending notification to ', user_id);
  if (!event.data.exists) {
    return console.log('User ', user_id, 'cancelled the friend request');
  }
  const fcmTokens = db.collection('/users').doc(sent_user_id).get();
  const sentUserProfile = admin.auth().getUser(sent_user_id);
  return Promise.all([fcmTokens, sentUserProfile]).then((results) => {
    const fcmTokens = results[0];
    const sentUserProfile = results[1];
    if (!fcmTokens.exists) {
      return console.log('There are no notification tokens to send to for', user_id);
    }
    const tokens = Object.keys(fcmTokens.data().fcm_tokens);
    console.log('There are', tokens.length, 'tokens to send notifications to for ', user_id);
    console.log('Fetched follower profile', sentUserProfile);

    const payload = {
      notification: {
        title: 'You have a new Friend Request!',
        body: `${sentUserProfile.displayName} is now following you.`,
        icon: `${sentUserProfile.photoURL}`
      },
      data: {
        title: 'You have a new Friend Request!',
        action: 'friend_request',
        body: `${sentUserProfile.displayName} is now following you.`,
        icon: `${sentUserProfile.photoURL}`
      }
    };

    return admin.messaging().sendToDevice(tokens, payload);
  }).then((response) => {
    // For each message check if there was an error.
    const tokensToRemove = [];
    response.results.forEach((result, index) => {
      const error = result.error;
      if (error) {
        console.error('Failure sending notification to', fcmTokens.data().fcm_tokens[index], error);
        // Cleanup the tokens who are not registered anymore.
        if (error.code === 'messaging/invalid-registration-token' || error.code === 'messaging/registration-token-not-registered') {
          tokensToRemove.push(fcmTokens.ref.child(tokens[index]).remove());
        }
      }
    });
    return Promise.all(tokensToRemove);
  });
});