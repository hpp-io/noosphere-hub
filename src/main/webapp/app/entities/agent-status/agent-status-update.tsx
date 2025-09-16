import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { createEntity, getEntity, reset, updateEntity } from './agent-status.reducer';

export const AgentStatusUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agents = useAppSelector(state => state.noospherehub.agent.entities);
  const agentStatusEntity = useAppSelector(state => state.noospherehub.agentStatus.entity);
  const loading = useAppSelector(state => state.noospherehub.agentStatus.loading);
  const updating = useAppSelector(state => state.noospherehub.agentStatus.updating);
  const updateSuccess = useAppSelector(state => state.noospherehub.agentStatus.updateSuccess);

  const handleClose = () => {
    navigate(`/noospherehub/agent-status${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAgents({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.lastKeepAliveAt = convertDateTimeToServer(values.lastKeepAliveAt);

    const entity = {
      ...agentStatusEntity,
      ...values,
      agent: agents.find(it => it.id.toString() === values.agent?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          lastKeepAliveAt: displayDefaultDateTime(),
        }
      : {
          ...agentStatusEntity,
          createdAt: convertDateTimeFromServer(agentStatusEntity.createdAt),
          lastKeepAliveAt: convertDateTimeFromServer(agentStatusEntity.lastKeepAliveAt),
          agent: agentStatusEntity?.agent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nooSphereHubApp.nooSphereHubAgentStatus.home.createOrEditLabel" data-cy="AgentStatusCreateUpdateHeading">
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgentStatus.home.createOrEditLabel">Create or edit a AgentStatus</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="agent-status-id"
                  label={translate('nooSphereHubApp.nooSphereHubAgentStatus.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgentStatus.createdAt')}
                id="agent-status-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgentStatus.lastKeepAliveAt')}
                id="agent-status-lastKeepAliveAt"
                name="lastKeepAliveAt"
                data-cy="lastKeepAliveAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="agent-status-agent"
                name="agent"
                data-cy="agent"
                label={translate('nooSphereHubApp.nooSphereHubAgentStatus.agent')}
                type="select"
              >
                <option value="" key="0" />
                {agents
                  ? agents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/noospherehub/agent-status" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AgentStatusUpdate;
