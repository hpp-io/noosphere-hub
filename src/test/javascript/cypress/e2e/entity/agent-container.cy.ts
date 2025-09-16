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

describe('AgentContainer e2e test', () => {
  const agentContainerPageUrl = '/noospherehub/agent-container';
  const agentContainerPageUrlPattern = new RegExp('/noospherehub/agent-container(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const agentContainerSample = { statusCode: 'during geez apropos', createdAt: '2025-09-15T14:53:37.863Z' };

  let agentContainer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/noospherehub/api/agent-containers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/noospherehub/api/agent-containers').as('postEntityRequest');
    cy.intercept('DELETE', '/services/noospherehub/api/agent-containers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (agentContainer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/noospherehub/api/agent-containers/${agentContainer.id}`,
      }).then(() => {
        agentContainer = undefined;
      });
    }
  });

  it('AgentContainers menu should load AgentContainers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('noospherehub/agent-container');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AgentContainer').should('exist');
    cy.url().should('match', agentContainerPageUrlPattern);
  });

  describe('AgentContainer page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agentContainerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AgentContainer page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/noospherehub/agent-container/new$'));
        cy.getEntityCreateUpdateHeading('AgentContainer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentContainerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/noospherehub/api/agent-containers',
          body: agentContainerSample,
        }).then(({ body }) => {
          agentContainer = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/noospherehub/api/agent-containers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/noospherehub/api/agent-containers?page=0&size=20>; rel="last",<http://localhost/services/noospherehub/api/agent-containers?page=0&size=20>; rel="first"',
              },
              body: [agentContainer],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(agentContainerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AgentContainer page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agentContainer');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentContainerPageUrlPattern);
      });

      it('edit button click should load edit AgentContainer page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentContainer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentContainerPageUrlPattern);
      });

      it('edit button click should load edit AgentContainer page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentContainer');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentContainerPageUrlPattern);
      });

      it('last delete button click should delete instance of AgentContainer', () => {
        cy.intercept('GET', '/services/noospherehub/api/agent-containers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('agentContainer').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentContainerPageUrlPattern);

        agentContainer = undefined;
      });
    });
  });

  describe('new AgentContainer page', () => {
    beforeEach(() => {
      cy.visit(`${agentContainerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AgentContainer');
    });

    it('should create an instance of AgentContainer', () => {
      cy.get(`[data-cy="statusCode"]`).type('wicked brief highly');
      cy.get(`[data-cy="statusCode"]`).should('have.value', 'wicked brief highly');

      cy.get(`[data-cy="createdAt"]`).type('2025-09-15T15:48');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-09-15T15:48');

      cy.get(`[data-cy="updatedAt"]`).type('2025-09-15T18:08');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-09-15T18:08');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        agentContainer = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', agentContainerPageUrlPattern);
    });
  });
});
