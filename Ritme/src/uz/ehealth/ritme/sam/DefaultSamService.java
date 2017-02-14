package uz.ehealth.ritme.sam;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.comparison.ATCMedicationCreator;
import uz.ehealth.ritme.core.ConfigHelper;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.emv.sam.v1.db.tables.xml.XMLObjectPersistor;
import uz.emv.sam.v1.domain.*;
import uz.emv.sam.v1.service.SamObjectRepository;
import uz.emv.sam.v1.service.xml.SamObjectRepositoryFactory;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by bdcuyp0 on 24-9-2015.
 */
public class DefaultSamService implements SamService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSamService.class);
    private SamObjectRepository samObjectRepository;
    private boolean updating;
    private boolean initialized;

    public DefaultSamService() {
        super();
    }

    private synchronized void initialize() {
        if (!initialized) {
            Properties properties = new Properties();
            try {
                properties.load(DefaultSamService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
                String fileLocation = properties.getProperty("ritme.sam.xml.location");
                samObjectRepository = SamObjectRepositoryFactory.createRepositoryFromXml(new File(fileLocation), null, new XMLObjectPersistor());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);

                update("user", "nihiiOrg");

            }
            if (samObjectRepository == null) {
                throw new IllegalStateException("samObjectRepository not initialized");
            }
            initialized = true;
        }
    }

    @Override
    public synchronized void update(String user, String nihiiOrg) {
        if (!updating) {
            updating = true;
            try {
                LOG.info("Available SAM version {}", PluginManager.get("ritme.sam.data", SamDataService.class).getActualSamDataVersion(user, nihiiOrg));
                LOG.info("Persisted SAM version {}", samObjectRepository == null ? "0" : samObjectRepository.getVersion());
                Integer nieuw;
                Integer oud;

                nieuw = Integer.parseInt(PluginManager.get("ritme.sam.data", SamDataService.class).getActualSamDataVersion(user, nihiiOrg));
                oud = Integer.parseInt(samObjectRepository == null ? "0" : samObjectRepository.getVersion());
                if (oud < nieuw) {
                    InputStream stream = PluginManager.get("ritme.sam.data", SamDataService.class).getActualSamDataAsZipStream(user, nihiiOrg);
                    samObjectRepository = SamObjectRepositoryFactory.createRepositoryFromXmlZipInputStream(stream, null, new XMLObjectPersistor());
                    LOG.info("Replaced SAM {} with SAM {}: for faster start-up persist SAM version {}", samObjectRepository == null ? "0" : samObjectRepository.getVersion(), PluginManager.get("ritme.sam.data", SamDataService.class).getActualSamDataVersion(user, nihiiOrg), PluginManager.get("ritme.sam.data", SamDataService.class).getActualSamDataVersion(user, nihiiOrg));
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            updating = false;
        }


    }

    @Override
    @NotNull
    public List<AMP> getAMPById(@NotNull final Long id) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPById(id);
    }

    @Override
    @NotNull
    public List<VMP> getVMPByCnk(final Integer cnk) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVMPByCnk(cnk);
    }

    @Override
    @NotNull
    public List<AMPP> getAMPPByCnk(final Integer cnk) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPPByCnk(cnk);
    }

    @Override
    @NotNull
    public List<VTM> getVTMByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVTMByName(name);
    }

    @Override
    @NotNull
    public List<VMPP> getVMPPByVMPName(final String vmpName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVMPPByVMPName(vmpName);
    }

    @Override
    @NotNull
    public List<VMPComb> getVMPCombByParentName(final String parentName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVMPCombByParentName(parentName);
    }

    @Override
    @NotNull
    public List<VMP> getVMPByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVMPByName(name);
    }

    @Override
    @NotNull
    public List<VirtualIngredientStrength> getVirtualIngredientStrengthByVTMName(final String vtmName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVirtualIngredientStrengthByVTMName(vtmName);
    }

    @Override
    @NotNull
    public List<VirtualIngredient> getVirtualIngredientByVTMName(final String vtmName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVirtualIngredientByVTMName(vtmName);
    }

    @Override
    @NotNull
    public List<TreatmentDurationCategory> getTreatmentDurationCategoryByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getTreatmentDurationCategoryByName(name);
    }

    @Override
    @NotNull
    public List<Substance> getSubstanceByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getSubstanceByName(name);
    }

    @Override
    @NotNull
    public List<RouteOfAdministration> getRouteOfAdministrationByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getRouteOfAdministrationByName(name);
    }

    @Override
    @NotNull
    public List<PharmaceuticalForm> getPharmaceuticalFormByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getPharmaceuticalFormByName(name);
    }

    @Override
    @NotNull
    public List<InnerPackage> getInnerPackageByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getInnerPackageByName(name);
    }

    @Override
    @NotNull
    public List<Company> getCompanyByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getCompanyByName(name);
    }

    @Override
    @NotNull
    public List<ATM> getATMByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getATMByName(name);
    }

    @Override
    @NotNull
    public List<ATC> getATCByAtcCv(final String atcCv) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getATCByAtcCv(atcCv);
    }

    @Override
    @NotNull
    public List<ATC> getATCByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getATCByName(name);
    }

    @Override
    @NotNull
    public List<Application> getApplicationByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getApplicationByName(name);
    }

    @Override
    @NotNull
    public List<AMPP> getAMPPByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPPByName(name);
    }

    @Override
    @NotNull
    public List<AMPIntPckComb> getAMPIntPckCombByyAMPParentName(final String ampParentName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPIntPckCombByyAMPParentName(ampParentName);
    }

    @Override
    @NotNull
    public List<AMPIntermediatePackage> getAMPIntermediatePackageByAMPName(final String ampName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPIntermediatePackageByAMPName(ampName);
    }

    @Override
    @NotNull
    public List<AMPComb> getAMPCombByParentName(final String parentName) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPCombByParentName(parentName);
    }

    @Override
    @NotNull
    public List<AMP> getAMPByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAMPByName(name);
    }

    @Override
    @NotNull
    public List<AdministrationForm> getAdministrationFormByName(final String name) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getAdministrationFormByName(name);
    }


    @NotNull
    @Override
    public List<ATM> getATMById(final Long atmId) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getATMById(atmId);
    }

    @NotNull
    @Override
    public List<VMP> getVMPById(final Integer integer) {
        if (!initialized) {
            initialize();
        }
        return samObjectRepository.getVMPById(integer);
    }

    @Override
    public Set<ATC> getATCForMedication(final MedicationIdType type, final String id) {
        if (!initialized) {
            initialize();
        }
        Set<AMP> amps = new HashSet<AMP>();
        switch (type) {
            case EAN:
            case VMPP:
                break;
            case CNK:
                final List<AMPP> ampps = this.getAMPPByCnk(Integer.parseInt(id));

                for (AMPP ampp : ampps) {
                    amps.add(ampp.getAMPIntermediatePackage().getAMP());
                }
                break;
            case INN:
                final List<VMP> vmps = this.getVMPById(Integer.parseInt(id));

                for (VMP vmp : vmps) {
                    amps.addAll(vmp.getAMPs());
                }
                break;
            case MAG:
                break;
            case AMP:
                amps.addAll(this.getAMPById(Long.parseLong(id)));
                break;
            case ATM:
                final List<ATM> atms = this.getATMById(Long.parseLong(id));
                for (ATM atm : atms) {
                    amps.addAll(atm.getAMPs());
                }
                break;
            case VTM:
                /*
                final List<VTM> vtms = this.getVTMById(Long.parseLong(id));
                for (VTM vtm : vtms) {
                    for (VMP vmp : vtm.getVMPs()) {
                        amps.addAll(vmp.getAMPs());
                    }
                }
                */
                break;
            case ATC:
                return new HashSet<ATC>(this.getATCByAtcCv(id));

        }


        Set<ATC> atcs = new HashSet<ATC>();
        for (AMP amp : amps) {
            atcs.add(amp.getATC());
        }
        return atcs;
    }

    @Override
    public Set<AMPP> getAMPPsForMedication(final Medication medication) {
        if (!initialized) {
            initialize();
        }
        Object object = getObjectForMedication(medication);
        @SuppressWarnings("unchecked")
        Set<AMPP> untyped = getChildrenForObject(object);
        return untyped;

    }

    @SuppressWarnings("unchecked")
    private Set getChildrenForObject(final Object object) {

        Set result = new HashSet();
        Set children = selectChildrenForObject(object);

        for (Object child : children) {

            if (!(child instanceof AMPP)) {

                result.addAll(getChildrenForObject(child));
            } else {

                result.add(child);
            }
        }
        return result;
    }

    private Set selectChildrenForObject(final Object object) {
        if (object instanceof ATC) {
            ATC atc = (ATC) object;
            int level = ATCMedicationCreator.getLevel(atc.getAtcCv());
            if (level < 5) {
                return atc.getChildren();
            } else {
                return atc.getAMPs();
            }
        } else if (object instanceof VTM) {
            VTM vtm = (VTM) object;
            return vtm.getATMs();
        } else if (object instanceof ATM) {
            ATM atm = (ATM) object;
            return atm.getAMPs();
        } else if (object instanceof VMP) {
            VMP vmp = (VMP) object;
            return vmp.getAMPs();
        } else if (object instanceof AMP) {
            AMP amp = (AMP) object;
            return amp.getAMPIntermediatePackages();
        } else if (object instanceof AMPIntermediatePackage) {
            AMPIntermediatePackage ampIntermediatePackage = (AMPIntermediatePackage) object;
            return ampIntermediatePackage.getAMPPs();
        } else if (object instanceof VMPP) {
            VMPP vmpp = (VMPP) object;
            return vmpp.getAMPPs();
        } else {
            return Collections.emptySet();
        }

    }


    private Object getObjectForMedication(final Medication medication) {
        if (medication == null) {
            return null;
        }
        switch (medication.getMedicationIdType()) {

            case EAN:
                break;
            case CNK:
                final List<AMPP> amppByCnk = this.getAMPPByCnk(Integer.valueOf(medication.getMedicationId()));
                if (!amppByCnk.isEmpty()) {
                    return amppByCnk.get(0);
                } else {
                    return null;
                }
            case INN:
                final List<VMP> vmpByCnk = this.getVMPByCnk(Integer.valueOf(medication.getMedicationId()));
                if (!vmpByCnk.isEmpty()) {
                    return vmpByCnk.get(0);
                } else {
                    return null;
                }
            case MAG:
                break;
            case AMP:
                final List<AMP> ampById = this.getAMPById(Long.valueOf(medication.getMedicationId()));
                if (!ampById.isEmpty()) {
                    return ampById.get(0);
                } else {
                    return null;
                }
            case VMPP:
                return null;
            case ATM:
                final List<ATM> atmById = this.getATMById(Long.valueOf(medication.getMedicationId()));
                if (!atmById.isEmpty()) {
                    return atmById.get(0);
                } else {
                    return null;
                }
            case VTM:
                return null;
            case ATC:
                final List<ATC> atcByAtcCv = getATCByAtcCv(medication.getMedicationId());
                if (!atcByAtcCv.isEmpty()) {
                    return atcByAtcCv.get(0);
                } else {
                    return null;
                }
        }
        return null;

    }

}
