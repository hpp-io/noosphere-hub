import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agent-status.reducer';

export const AgentStatusDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agentStatusEntity = useAppSelector(state => state.noospherehub.agentStatus.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agentStatusDetailsHeading">
          <Translate contentKey="nooSphereHubApp.nooSphereHubAgentStatus.detail.title">AgentStatus</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentStatus.id">Id</Translate>
            </span>
          </dt>
          <dd>{agentStatusEntity.id}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentStatus.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {agentStatusEntity.createdAt ? <TextFormat value={agentStatusEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastKeepAliveAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentStatus.lastKeepAliveAt">Last Keep Alive At</Translate>
            </span>
          </dt>
          <dd>
            {agentStatusEntity.lastKeepAliveAt ? (
              <TextFormat value={agentStatusEntity.lastKeepAliveAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgentStatus.agent">Agent</Translate>
          </dt>
          <dd>{agentStatusEntity.agent ? agentStatusEntity.agent.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/noospherehub/agent-status" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/noospherehub/agent-status/${agentStatusEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgentStatusDetail;
