import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { getEntities as getContainers } from 'app/entities/container/container.reducer';
import { createEntity, getEntity, updateEntity } from './agent-container.reducer';

export const AgentContainerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agents = useAppSelector(state => state.noospherehub.agent.entities);
  const containers = useAppSelector(state => state.noospherehub.container.entities);
  const agentContainerEntity = useAppSelector(state => state.noospherehub.agentContainer.entity);
  const loading = useAppSelector(state => state.noospherehub.agentContainer.loading);
  const updating = useAppSelector(state => state.noospherehub.agentContainer.updating);
  const updateSuccess = useAppSelector(state => state.noospherehub.agentContainer.updateSuccess);

  const handleClose = () => {
    navigate('/noospherehub/agent-container');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getAgents({}));
    dispatch(getContainers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...agentContainerEntity,
      ...values,
      node: agents.find(it => it.id.toString() === values.node?.toString()),
      container: containers.find(it => it.id.toString() === values.container?.toString()),
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
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...agentContainerEntity,
          createdAt: convertDateTimeFromServer(agentContainerEntity.createdAt),
          updatedAt: convertDateTimeFromServer(agentContainerEntity.updatedAt),
          node: agentContainerEntity?.node?.id,
          container: agentContainerEntity?.container?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nooSphereHubApp.nooSphereHubAgentContainer.home.createOrEditLabel" data-cy="AgentContainerCreateUpdateHeading">
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.home.createOrEditLabel">
              Create or edit a AgentContainer
            </Translate>
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
                  id="agent-container-id"
                  label={translate('nooSphereHubApp.nooSphereHubAgentContainer.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgentContainer.statusCode')}
                id="agent-container-statusCode"
                name="statusCode"
                data-cy="statusCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgentContainer.createdAt')}
                id="agent-container-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgentContainer.updatedAt')}
                id="agent-container-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="agent-container-node"
                name="node"
                data-cy="node"
                label={translate('nooSphereHubApp.nooSphereHubAgentContainer.node')}
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
              <ValidatedField
                id="agent-container-container"
                name="container"
                data-cy="container"
                label={translate('nooSphereHubApp.nooSphereHubAgentContainer.container')}
                type="select"
              >
                <option value="" key="0" />
                {containers
                  ? containers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button
                tag={Link}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/noospherehub/agent-container"
                replace
                color="info"
              >
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

export default AgentContainerUpdate;
