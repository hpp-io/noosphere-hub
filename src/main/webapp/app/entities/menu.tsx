import React, { useEffect } from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { addTranslationSourcePrefix } from 'app/shared/reducers/locale';
import { useAppDispatch, useAppSelector } from 'app/config/store';

const EntitiesMenu = () => {
  const lastChange = useAppSelector(state => state.locale.lastChange);
  const dispatch = useAppDispatch();
  useEffect(() => {
    dispatch(addTranslationSourcePrefix('services/noospherehub/'));
  }, [lastChange]);

  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/noospherehub/agent">
        <Translate contentKey="global.menu.entities.nooSphereHubAgent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/noospherehub/container">
        <Translate contentKey="global.menu.entities.nooSphereHubContainer" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/noospherehub/agent-container">
        <Translate contentKey="global.menu.entities.nooSphereHubAgentContainer" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/noospherehub/agent-status">
        <Translate contentKey="global.menu.entities.nooSphereHubAgentStatus" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
