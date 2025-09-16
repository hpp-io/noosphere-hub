import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgentContainer from './agent-container';
import AgentContainerDetail from './agent-container-detail';
import AgentContainerUpdate from './agent-container-update';
import AgentContainerDeleteDialog from './agent-container-delete-dialog';

const AgentContainerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgentContainer />} />
    <Route path="new" element={<AgentContainerUpdate />} />
    <Route path=":id">
      <Route index element={<AgentContainerDetail />} />
      <Route path="edit" element={<AgentContainerUpdate />} />
      <Route path="delete" element={<AgentContainerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AgentContainerRoutes;
