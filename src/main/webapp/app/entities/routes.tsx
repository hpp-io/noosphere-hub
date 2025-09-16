import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { ReducersMapObject, combineReducers } from '@reduxjs/toolkit';

import getStore from 'app/config/store';

import entitiesReducers from './reducers';

import Agent from './agent';
import Container from './container';
import AgentContainer from './agent-container';
import AgentStatus from './agent-status';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  const store = getStore();
  store.injectReducer('noospherehub', combineReducers(entitiesReducers as ReducersMapObject));
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/agent/*" element={<Agent />} />
        <Route path="/container/*" element={<Container />} />
        <Route path="/agent-container/*" element={<AgentContainer />} />
        <Route path="/agent-status/*" element={<AgentStatus />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
