package org.bouncycastle.pqc.jcajce.provider.ntru;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.SpecUtil;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;

public class NTRUKeyPairGeneratorSpi
        extends java.security.KeyPairGenerator
{
    private static Map parameters = new HashMap();

    static
    {
        parameters.put(NTRUParameterSpec.ntruhps2048509.getName(), NTRUParameters.ntruhps2048509);
        parameters.put(NTRUParameterSpec.ntruhps2048677.getName(), NTRUParameters.ntruhps2048677);
        parameters.put(NTRUParameterSpec.ntruhps4096821.getName(), NTRUParameters.ntruhps4096821);
        parameters.put(NTRUParameterSpec.ntruhrss701.getName(), NTRUParameters.ntruhrss701);
    }

    NTRUKeyGenerationParameters param;
    NTRUKeyPairGenerator engine = new NTRUKeyPairGenerator();

    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public NTRUKeyPairGeneratorSpi()
    {
        super("NTRU");
    }

    public void initialize(
            int strength,
            SecureRandom random)
    {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    public void initialize(
            AlgorithmParameterSpec params,
            SecureRandom random)
            throws InvalidAlgorithmParameterException
    {
        if (!(params instanceof NTRUParameterSpec))
        {
            throw new InvalidAlgorithmParameterException("parameter object not a NTRUParameterSpec");
        }

        param = new NTRUKeyGenerationParameters(random, (NTRUParameters)parameters.get(getNameFromParams(params)));

        engine.init(param);
        initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec paramSpec)
            throws InvalidAlgorithmParameterException
    {
        if (paramSpec instanceof NTRUParameterSpec)
        {
            NTRUParameterSpec frodoParams = (NTRUParameterSpec)paramSpec;
            return frodoParams.getName();
        }
        else
        {
            return SpecUtil.getNameFrom(paramSpec);
        }
    }

    public KeyPair generateKeyPair()
    {
        if (!initialised)
        {
            param = new NTRUKeyGenerationParameters(random, NTRUParameters.ntruhps2048509);

            engine.init(param);
            initialised = true;
        }

        AsymmetricCipherKeyPair pair = engine.generateKeyPair();
        NTRUPublicKeyParameters pub = (NTRUPublicKeyParameters)pair.getPublic();
        NTRUPrivateKeyParameters priv = (NTRUPrivateKeyParameters)pair.getPrivate();

        return new KeyPair(new BCNTRUPublicKey(pub), new BCNTRUPrivateKey(priv));
    }
}
