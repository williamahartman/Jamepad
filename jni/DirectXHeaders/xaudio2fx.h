/**************************************************************************
 *
 * Copyright (c) Microsoft Corporation.  All rights reserved.
 *
 * File:    xaudio2.h
 * Content: Declarations for the audio effects included with XAudio2.
 *
 **************************************************************************/

#ifndef __XAUDIO2FX_INCLUDED__
#define __XAUDIO2FX_INCLUDED__


/**************************************************************************
 *
 * XAudio2 effect class IDs.
 *
 **************************************************************************/

#include <comdecl.h>  // For DEFINE_CLSID and DEFINE_IID

DEFINE_CLSID(AudioVolumeMeter, C0C56F46, 29B1, 44E9, 99, 39, A3, 2C, E8, 68, 67, E2);
DEFINE_CLSID(AudioVolumeMeter_Debug, C0C56F46, 29B1, 44E9, 99, 39, A3, 2C, E8, 68, 67, DB);
DEFINE_CLSID(AudioReverb, 6F6EA3A9, 2CF5, 41CF, 91, C1, 21, 70, B1, 54, 00, 63);
DEFINE_CLSID(AudioReverb_Debug, 6F6EA3A9, 2CF5, 41CF, 91, C1, 21, 70, B1, 54, 00, DB);


// Ignore the rest of this header if only the GUID definitions were requested
#ifndef GUID_DEFS_ONLY


/**************************************************************************
 *
 * XAudio2 effect parameter structures.
 *
 **************************************************************************/


// XAUDIO2FX_VOLUMEMETER_LEVELS: Receives results from GetMeterLevels().
// The user is responsible for allocating pPeakLevels, pRMSLevels, and
// initializing ChannelCount accordingly.
typedef struct XAUDIO2FX_VOLUMEMETER_LEVELS
{
    float* pPeakLevels;  // Peak levels table: receives maximum absolute level for each channel
                         // over a processing pass; must have at least ChannelCount elements
    float* pRMSLevels;   // Root mean square levels table: receives RMS level for each channel
                         // over a processing pass; must have at least ChannelCount elements
    UINT32 ChannelCount; // Number of channels being processed by the volume meter APO
} XAUDIO2FX_VOLUMEMETER_LEVELS;


// XAUDIO2FX_REVERB_PARAMETERS: Parameter set for the reverb effect.
typedef struct XAUDIO2FX_REVERB_PARAMETERS
{
    // Original / processed signal ratio
    float WetDryMix;            // [0, 100] (percentage)

    // Delay times
    UINT32 ReflectionsDelay;    // [0, 300] in ms
    BYTE ReverbDelay;           // [0, 85] in ms
    BYTE RearDelay;             // [0, 5] in ms

    // Indexed parameters
    BYTE PositionLeft;          // [0, 30] no units
    BYTE PositionRight;         // [0, 30] no units
    BYTE PositionMatrixLeft;    // [0, 30] no units
    BYTE PositionMatrixRight;   // [0, 30] no units
    BYTE EarlyDiffusion;        // [0, 15] no units
    BYTE LateDiffusion;         // [0, 15] no units
    BYTE LowEQGain;             // [0, 12] no units
    BYTE LowEQCutoff;           // [0, 9] no units
    BYTE HighEQGain;            // [0, 8] no units
    BYTE HighEQCutoff;          // [0, 14] no units

    // Direct parameters
    float RoomFilterFreq;       // [20, 20000] in Hz
    float RoomFilterMain;       // [-100, 0] in dB
    float RoomFilterHF;         // [-100, 0] in dB
    float ReflectionsGain;      // [-100, 20] in dB
    float ReverbGain;           // [-100, 20] in dB
    float DecayTime;            // [0.1, inf] in seconds
    float Density;              // [0, 100] (percentage)
    float RoomSize;             // [1, 100] in feet
} XAUDIO2FX_REVERB_PARAMETERS;


/**************************************************************************
 *
 * Effect creation functions.  On Windows, these are just inline functions
 * that call CoCreateInstance and Initialize; the XAUDIO2FX_DEBUG flag can
 * be used to select the debug version of the effects.  On Xbox, these map
 * to real functions included in xaudio2.lib, and the XAUDIO2FX_DEBUG flag
 * is ignored; the application must link with the debug library to use the
 * debug functionality.
 *
 **************************************************************************/

// Use default values for some parameters if building C++ code
#ifdef __cplusplus
    #define DEFAULT(x) =x
#else
    #define DEFAULT(x)
#endif

#define XAUDIO2FX_DEBUG 1   // To select the debug version of an effect

#ifdef _XBOX

    STDAPI CreateAudioVolumeMeter(IUnknown** ppApo);
    STDAPI CreateAudioReverb(IUnknown** ppApo);

    __inline HRESULT XAudio2CreateVolumeMeter(IUnknown** ppApo, UINT32 /*Flags*/ DEFAULT(0))
    {
        return CreateAudioVolumeMeter(ppApo);
    }

    __inline HRESULT XAudio2CreateReverb(IUnknown** ppApo, UINT32 /*Flags*/ DEFAULT(0))
    {
        return CreateAudioReverb(ppApo);
    }

#else // Windows

    __inline HRESULT XAudio2CreateVolumeMeter(IUnknown** ppApo, UINT32 Flags DEFAULT(0))
    {
        #ifdef __cplusplus
            return CoCreateInstance((Flags & XAUDIO2FX_DEBUG) ? __uuidof(AudioVolumeMeter_Debug)
                                                              : __uuidof(AudioVolumeMeter),
                                    NULL, CLSCTX_INPROC_SERVER, __uuidof(IUnknown), (void**)ppApo);
        #else
            return CoCreateInstance((Flags & XAUDIO2FX_DEBUG) ? &CLSID_AudioVolumeMeter_Debug
                                                              : &CLSID_AudioVolumeMeter,
                                    NULL, CLSCTX_INPROC_SERVER, &IID_IUnknown, (void**)ppApo);
        #endif
    }

    __inline HRESULT XAudio2CreateReverb(IUnknown** ppApo, UINT32 Flags DEFAULT(0))
    {
        #ifdef __cplusplus
            return CoCreateInstance((Flags & XAUDIO2FX_DEBUG) ? __uuidof(AudioReverb_Debug)
                                                              : __uuidof(AudioReverb),
                                    NULL, CLSCTX_INPROC_SERVER, __uuidof(IUnknown), (void**)ppApo);
        #else
            return CoCreateInstance((Flags & XAUDIO2FX_DEBUG) ? &CLSID_AudioReverb_Debug
                                                              : &CLSID_AudioReverb,
                                    NULL, CLSCTX_INPROC_SERVER, &IID_IUnknown, (void**)ppApo);
        #endif
    }

#endif // #ifdef _XBOX
#endif // #ifndef GUID_DEFS_ONLY
#endif // #ifndef __XAUDIO2FX_INCLUDED__
