import agent from 'app/entities/agent/agent.reducer';
import container from 'app/entities/container/container.reducer';
import agentContainer from 'app/entities/agent-container/agent-container.reducer';
import agentStatus from 'app/entities/agent-status/agent-status.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  agent,
  container,
  agentContainer,
  agentStatus,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
