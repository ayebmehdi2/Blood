exports.sendNotification = functions.database.ref('/notifications/messages/{pushId}')
    .onWrite(event => {
        const message = event.data.current.val();
        const senderUid = message.id;
        const receiverUid = message.hisId;
        const promises = [];

        if (senderUid === receiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/PROFILES/${receiverUid}`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(receiverUid);

        return Promise.all([getInstanceIdPromise, getReceiverUidPromise]).then(results => {
            const instanceId = results[0].val();
            const receiver = results[1];
            console.log('notifying ' + receiverUid + ' about ' + message.conStr + ' from ' + senderUid);

            const payload = {
                notification: {
                    title: receiver.name,
                    body: message.conStr,
                    icon: receiver.photo
                }
            };

            admin.messaging().sendToDevice(instanceId, payload);
            return console.log("Notification Sent.");

          });
    });