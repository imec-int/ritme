package be.business.connector.common.mock;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import be.business.connector.common.CommonIntegrationModuleImpl;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.ConfigUtils;
import be.business.connector.core.utils.EncryptionUtils;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.IOUtils;
import be.business.connector.projects.common.utils.SystemServicesUtils;


public class CommonIntegrationModuleMock extends CommonIntegrationModuleImpl {
	
	private String samlToken = null;
	
	private EncryptionUtils encryptionUtils;
	
	public CommonIntegrationModuleMock() throws IntegrationModuleException {
		super();
	}
	
	
	@Override
	public void unloadSession(final String nihiiOrg) {}
	
	@Override
	public void loadSession(String token, final String nihiiOrg) throws IntegrationModuleException {}
	
	@Override
	public String createSession(final String nihiiOrg) throws IntegrationModuleException {
		String s = "H4sIAAAAAAAAAO1YW4/aOBR+npX2P0Tpy640ITcuiUWmYijtog60CzNV1ZeRiU/AbYhRbGZ6Uf/7OiExCQWGGam7WqkvKD4+t+/cbNx9/nkZa3eQcsqSQLcblq5BEjJCk3mg31y/NDz9+cXvv3WnaJDcQcxWoEmJhKNpoC+EWCHT5OEClpg3JJ0zvGqwdG5mHyYUEqYuNZxJFZeMfMk+z7oT4CuW8EJZoK/TBDHMKUcJXgJHIkTT3ugKSYfQKmWChSzWC8scL+PjEphzSIVEVBVZnWplmJTeXbNAv3VtlzQ90vbdyMPgtX2bECdqt5sukHariaUA52sYJlzgRAS6Y9mWYbmG7VzbDrJ9ZDUbHa/1QddG+CNL36lYSwJN6oQJhHRFIVOT+ToDFM3ZHYIF4FgsEOcsjLPtjHPj4vBFoOOZg8Fymx3sN2fYB8+x7VkLY8v3HS/0nDz8MuhTgcWabxblqs8IaO9wvIZAz6OEpuswBM51s5Ayq2LdXhnaUzK3Nw8/M3Ubkc+cqOK8v79v3Lt5TTqWZZvvR1fTvF63zPRhZoPm2Q1B11QAsshHnteZebZlR5Hj4oi4Lcub+bjphlHHDcPOodqw/GvbQq0WcjoNt93+UPClB9IuOLJvrRMKqMxtnyWEZk5ybczEJUQshWPWJdOb5E3ai0TmQ5XPrvKZpf7eWixkGdIQZ0ayAoGlXGt1+gmw6wIjEAtGHiiQJXrfsnzj7ethCVfW8nr2EUJRrs+6Yyk3JJnmiEKqvWTpEotjiu2cQokR5ay5jUJrpkuGSP7+vcZxrjDQ+8Hl4Fzrj4Pp20F/OBqMtb4M+FdItH5Pv1C7vZiGoJU8f0zpPJHNlMKf59r0ZjLujQZKwbn2avhuMM5pG7FXEBM6h08Yp8K12k0pM5gMe1fjm9HlYBJ07I7dsi3L6lhdsw54G4gCgyyIiGbAZJTVZl4nirwJ/sWx2IdLtGAxgdRgkfEJvnTNPQq22vfYfoEF3nJUx0mtr24dv4Ujp+3NWkBmjh950MLtNiEYolk7jOyHG8tueB230lj/dkpO71aVipNatsT1UMsWfGbNSE+IlM7WAlTLVrd/7KNfzVRtprPNcbgnRtvAauorU1Kf5yuZepbIU5zK063Gx1c43GGmhf3NZDSSkkvfSY7Skx/iFzUcO3t1HGrzCUjKkykfClnb5n7CZjj8H3BWyPtaQW6X46gyz8wHB1qXcPQavgyTiBXXC8IP3C4s0/JNyUM4nT+rgs10ZA2zMyoVvb+N98W3SvA1eQ1ikfbXZkC/iQzpyPeuuUeqinOvsZxc4NgeJfvgqxN4py1kAPdfEdQN4mD4fx3mB+bPD6PnP5k6T2jEPT34aNfRkpJ7GgGaMRYDPgFK0RpAtijKuXUYjUjXcDqMI1Okm/U1UiWipgGdP2oebNUAqXdjvtHHCUtksmL6tXIF03rxnKVULJYH/9nYVmbLgM+hEdrNpDqAupIoSWiYhPGa07ttfHmBYsNwum6zMkOOuL2DTcXuRFC1AKYcG3yB7artXOsEIkhB/pPTbibDQH/24D+42nCXCq5TnPBsrvD6zK5uPc7X8pmEGLyEXLu2PVb50ez+3PyqDCtv64fKgfhtEL6Q45GLp2R7J9M1fZsW/kbyhaaeF74X7lSZdqpUlcp2ABzoxp2CLUyqbGrF2PleUVBhrCn54dTNqXuOaEU/eB/YsXroFlDZrpooyHV/dgGUT0T121LXLF+n8nc/Uz38ZZ/lM+LFPzBmGE97FAAA";
		byte[] bytes = Base64.decode(s.getBytes());
		try {
			bytes = IOUtils.decompress(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		samlToken =  new String(bytes);
		return samlToken;
	}
	
	@Override
	public String createFallbackSession(String niss, String passphrase, final String nihiiOrg) throws IntegrationModuleException {
		return createSession(nihiiOrg);
	}
	
	@Override
	public boolean hasValidSession(final String nihiiOrg){
		return !StringUtils.isEmpty(samlToken);
	}
	
	@Override
	public void getLatestSystemServices(final String nihiiOrg) throws IntegrationModuleException {
		String systemServicesPath;
		if (getPropertyHandler().hasProperty("SYSTEM_SERVICES_PATH")) {
			systemServicesPath = getPropertyHandler().getProperty("SYSTEM_SERVICES_PATH");
		} else {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.get.system.services.failed"));
		}
		SystemServicesUtils ssu = SystemServicesUtils.getInstance();
		ssu.setSystemServicesXmlFile(ConfigUtils.getLatestSystemServicesFile(systemServicesPath,getPropertyHandler()));
		ssu.reloadCache();
	}
		
}
