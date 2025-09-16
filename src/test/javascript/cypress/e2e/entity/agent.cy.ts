import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Agent e2e test', () => {
  const agentPageUrl = '/noospherehub/agent';
  const agentPageUrlPattern = new RegExp('/noospherehub/agent(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const agentSample = {
    apiUrl: 'though biodegradable provided',
    apiKey: 'wallaby ouch',
    statusCode: 'busy',
    createdAt: '2025-09-15T17:58:43.720Z',
  };

  let agent;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/noospherehub/api/agents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/noospherehub/api/agents').as('postEntityRequest');
    cy.intercept('DELETE', '/services/noospherehub/api/agents/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (agent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/noospherehub/api/agents/${agent.id}`,
      }).then(() => {
        agent = undefined;
      });
    }
  });

  it('Agents menu should load Agents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('noospherehub/agent');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Agent').should('exist');
    cy.url().should('match', agentPageUrlPattern);
  });

  describe('Agent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Agent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/noospherehub/agent/new$'));
        cy.getEntityCreateUpdateHeading('Agent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/noospherehub/api/agents',
          body: agentSample,
        }).then(({ body }) => {
          agent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/noospherehub/api/agents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/noospherehub/api/agents?page=0&size=20>; rel="last",<http://localhost/services/noospherehub/api/agents?page=0&size=20>; rel="first"',
              },
              body: [agent],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(agentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Agent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });

      it('edit button click should load edit Agent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Agent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });

      it('edit button click should load edit Agent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Agent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });

      it('last delete button click should delete instance of Agent', () => {
        cy.intercept('GET', '/services/noospherehub/api/agents/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('agent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);

        agent = undefined;
      });
    });
  });

  describe('new Agent page', () => {
    beforeEach(() => {
      cy.visit(`${agentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Agent');
    });

    it('should create an instance of Agent', () => {
      cy.get(`[data-cy="name"]`).type('gradient');
      cy.get(`[data-cy="name"]`).should('have.value', 'gradient');

      cy.get(`[data-cy="apiUrl"]`).type('scarily unnaturally ew');
      cy.get(`[data-cy="apiUrl"]`).should('have.value', 'scarily unnaturally ew');

      cy.get(`[data-cy="apiKey"]`).type('impeccable');
      cy.get(`[data-cy="apiKey"]`).should('have.value', 'impeccable');

      cy.get(`[data-cy="statusCode"]`).type('adult mmm oh');
      cy.get(`[data-cy="statusCode"]`).should('have.value', 'adult mmm oh');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="createdAt"]`).type('2025-09-15T13:34');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-09-15T13:34');

      cy.get(`[data-cy="updatedAt"]`).type('2025-09-15T06:34');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-09-15T06:34');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        agent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', agentPageUrlPattern);
    });
  });
});
