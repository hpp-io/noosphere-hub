import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgentStatus from './agent-status';
import AgentStatusDetail from './agent-status-detail';
import AgentStatusUpdate from './agent-status-update';
import AgentStatusDeleteDialog from './agent-status-delete-dialog';

const AgentStatusRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgentStatus />} />
    <Route path="new" element={<AgentStatusUpdate />} />
    <Route path=":id">
      <Route index element={<AgentStatusDetail />} />
      <Route path="edit" element={<AgentStatusUpdate />} />
      <Route path="delete" element={<AgentStatusDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AgentStatusRoutes;
