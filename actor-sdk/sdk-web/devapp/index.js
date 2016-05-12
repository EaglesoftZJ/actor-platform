/*
 * Copyright (C) 2015-2016 Actor LLC. <https://actor.im>
 */

import ActorSDK from '../src/sdk/actor-sdk';
import ActorSDKDelegate from '../src/sdk/actor-sdk-delegate';

const components = {};
const actions = {};
const l18n = {};

const options = {
  endpoints: [
              'ws://220.189.207.18:9080'
  ],
  delegate: new ActorSDKDelegate(components, actions, l18n),
  isExperimental: true
};

const app = new ActorSDK(options);

app.startApp();
//    'wss://front1-ws-mtproto-api-rev2.actor.im',
//    'wss://front2-ws-mtproto-api-rev2.actor.im'
